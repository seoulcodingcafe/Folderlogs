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

package io.github.folderlogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.util.Objects;

import io.github.folderlogs.db.Folder;
import io.github.folderlogs.ui.Alert;

public class MainActivity extends CyaneaAppCompatActivity {

	BottomNavigationView mNavView;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK && data.getData() != null) {
			try {
				Uri uri = data.getData();
				DocumentFile doc = DocumentFile.fromTreeUri(this, uri);
				String folderLabel = Objects.requireNonNull(doc).getName();
				final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
				getContentResolver().takePersistableUriPermission(uri, takeFlags);

				Folder folder = new Folder();
				if (folderLabel != null)
					folder.folderLabel = folderLabel;
				else
					folder.folderLabel = uri.toString();
				folder.folderUri = uri.toString();
				folder.insert(this);
			} catch (Exception ex) {
				new Alert(this).error(ex.getMessage());
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavView = findViewById(R.id.nav_view);
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_folders,
				R.id.navigation_logs, R.id.navigation_configuration, R.id.navigation_about).build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(mNavView, navController);
	}

	public void startAddFolderProcess() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
		startActivityForResult(intent, 1);
	}
}