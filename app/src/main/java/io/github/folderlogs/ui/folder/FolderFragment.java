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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import id.ionbit.ionalert.IonAlert;
import io.github.folderlogs.MainActivity;
import io.github.folderlogs.R;
import io.github.folderlogs.db.FolderViewModel;
import io.github.folderlogs.tools.Scan;
import io.github.folderlogs.ui.Alert;

public class FolderFragment extends Fragment {
	private FolderListAdapter mAdapter;
	private EditText mSearch;
	private FolderViewModel mFolderViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_folders, container, false);
		Button manualScan = root.findViewById(R.id.manualScan);
		manualScan.setOnClickListener(v -> {
			IonAlert alert = new Alert(v.getContext()).scanning();
			alert.show();
			new Thread(() -> {
				try {
					int newLogs = new Scan(v.getContext()).run();
					v.post(() -> {
						alert.dismissWithAnimation();
						new Alert(v.getContext()).detectedChanges(newLogs);
					});
				} catch (Exception e) {
					v.post(() -> {
						new Alert(v.getContext()).error(e.getMessage());
					});
				}
			}).start();
		});
		mAdapter = new FolderListAdapter(getActivity());
		RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(mAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mFolderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);
		mFolderViewModel.getAllFolder().observe(getViewLifecycleOwner(), folders -> mAdapter.setFolders(folders));
		mSearch = root.findViewById(R.id.search);
		mSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				search();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		root.findViewById(R.id.addFolder).setOnClickListener(v -> {
			((MainActivity) requireActivity()).startAddFolderProcess();
		});

		return root;
	}

	private void search() {
		if (mSearch.getText().length() < 1)
			mFolderViewModel.getAllFolder().observe(getViewLifecycleOwner(), folders -> mAdapter.setFolders(folders));
		else
			mFolderViewModel.search(mSearch.getText().toString()).observe(getViewLifecycleOwner(),
					folders -> mAdapter.setFolders(folders));
	}
}
