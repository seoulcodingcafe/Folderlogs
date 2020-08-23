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

package io.github.folderlogs.ui.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import io.github.folderlogs.R;
import io.github.folderlogs.db.Log;
import io.github.folderlogs.ui.Alert;

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.LogViewHolder> {

	static class LogViewHolder extends RecyclerView.ViewHolder {
		private final TextView mTextViewLabel;
		private final TextView mTextViewNew;
		private final TextView mTextViewDateTime;
		private final View mBackGround;
		Log mLog;

		private LogViewHolder(View itemView) {
			super(itemView);
			mTextViewLabel = itemView.findViewById(R.id.label);
			mTextViewNew = itemView.findViewById(R.id.newLabel);
			mTextViewDateTime = itemView.findViewById(R.id.dateTime);
			mBackGround = itemView.findViewById(R.id.backGround);
		}

		private void open(Context context) {
			new Alert(context).openLog(mLog);
		}

		@SuppressLint("SetTextI18n")
		void updateContent() {
			String newOrModified;
			mTextViewDateTime.setText(new PrettyTime().format(new Date(mLog.createdAt)));

			if (!mLog.modified)
				newOrModified = mTextViewNew.getContext().getString(R.string.New);
			else
				newOrModified = mTextViewNew.getContext().getString(R.string.Modified);
			if (mLog.cantaccess)
				newOrModified = mTextViewNew.getContext().getString(R.string.ERROR);
			if (mLog.deleted)
				newOrModified = mTextViewNew.getContext().getString(R.string.Deleted);

			mTextViewNew.setText(newOrModified);

			if (mLog.cantaccess)
				mTextViewNew.setTextColor(Color.RED);
			else if (mLog.modified)
				mTextViewNew.setTextColor(Color.MAGENTA);
			else
				mTextViewNew.setTextColor(Color.GREEN);
			if (mLog.deleted)
				mTextViewNew.setTextColor(Color.YELLOW);

			mTextViewLabel.setText(mLog.label);

			mBackGround.setOnClickListener(v -> open(v.getContext()));
			mTextViewNew.setOnClickListener(v -> open(v.getContext()));
			mTextViewLabel.setOnClickListener(v -> open(v.getContext()));
			mTextViewDateTime.setOnClickListener(v -> open(v.getContext()));
		}
	}

	private final LayoutInflater mInflater;

	private List<Log> mLogs;

	LogListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		if (mLogs != null)
			return mLogs.size();
		else
			return 0;
	}

	@Override
	public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
		if (mLogs != null) {
			holder.mLog = mLogs.get(position);
			holder.updateContent();
		}
	}

	@Override
	public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mInflater.inflate(R.layout.recycler_log, parent, false);

		return new LogViewHolder(itemView);
	}

	void setLogs(List<Log> logs) {
		mLogs = logs;
		notifyDataSetChanged();
	}
}
