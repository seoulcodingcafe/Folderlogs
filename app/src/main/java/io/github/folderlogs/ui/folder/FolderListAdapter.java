//    The GNU General Public License does not permit incorporating this program
//    into proprietary programs.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

package io.github.folderlogs.ui.folder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import io.github.folderlogs.R;
import io.github.folderlogs.db.Folder;
import io.github.folderlogs.ui.Alert;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {
	static class FolderViewHolder extends RecyclerView.ViewHolder {
		private final TextView mTextViewLabel;
		private final TextView mTextViewDateTime;
		private final View mBackGround;
		private final ImageView mIcon;
		Folder mFolder;

		private FolderViewHolder(View itemView) {
			super(itemView);
			mTextViewLabel = itemView.findViewById(R.id.label);
			mTextViewDateTime = itemView.findViewById(R.id.dateTime);
			mBackGround = itemView.findViewById(R.id.backGround);
			mIcon = itemView.findViewById(R.id.folderIcon);
		}

		private void open(Context context) {
			new Alert(context).openFolder(mFolder);
		}

		@SuppressLint("SetTextI18n")
		void updateContent() {
			mTextViewDateTime.setText(mTextViewDateTime.getContext().getString(R.string.added) + ": "
					+ new PrettyTime().format(new Date(mFolder.createdAt)));
			mTextViewLabel.setText(mFolder.folderLabel);
			mBackGround.setOnClickListener(v -> open(v.getContext()));
			mTextViewLabel.setOnClickListener(v -> open(v.getContext()));
			mTextViewDateTime.setOnClickListener(v -> open(v.getContext()));
			mIcon.setOnClickListener(v -> open(v.getContext()));
		}
	}

	private final LayoutInflater mInflater;

	private List<Folder> mFolders;

	FolderListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		if (mFolders != null)
			return mFolders.size();
		else
			return 0;
	}

	@Override
	public void onBindViewHolder(@NonNull FolderListAdapter.FolderViewHolder holder, int position) {
		if (mFolders != null) {
			holder.mFolder = mFolders.get(position);
			holder.updateContent();
		}
	}

	@Override
	public FolderListAdapter.FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mInflater.inflate(R.layout.recycler_folder, parent, false);

		return new FolderListAdapter.FolderViewHolder(itemView);
	}

	void setFolders(List<Folder> folders) {
		mFolders = folders;
		notifyDataSetChanged();
	}
}
