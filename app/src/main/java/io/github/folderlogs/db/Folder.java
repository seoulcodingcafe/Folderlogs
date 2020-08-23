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

package io.github.folderlogs.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "folder_table")
public class Folder {
	public static LiveData<List<Folder>> all(Context context) {
		return Database.getDatabase(context).folderDao().all();
	}

	public static List<Folder> allPlain(Context context) {
		return Database.getDatabase(context).folderDao().allPlain();
	}

	public static Folder get(int id, Context context) {
		return Database.getDatabase(context).folderDao().get(id);
	}

	public static LiveData<List<Folder>> search(String searchWord, Context context) {
		return Database.getDatabase(context).folderDao().search(searchWord);
	}

	@PrimaryKey(autoGenerate = true)
	public int id;

	@NonNull
	public String folderLabel = "";

	@NonNull
	public String folderUri = "";

	public long createdAt = System.currentTimeMillis();

	public void delete(Context context) {
		Database.getDatabase(context).folderDao().delete(this);
	}

	public void insert(Context context) {
		createdAt = System.currentTimeMillis();
		Database.getDatabase(context).folderDao().insert(this);
	}

	public void update(Context context) {
		Database.getDatabase(context).folderDao().update(this);
	}
}
