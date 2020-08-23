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

import com.pixplicity.easyprefs.library.Prefs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import io.github.folderlogs.tools.BytesToString;

@Entity(tableName = "log_table")
public class Log {
	public static LiveData<List<Log>> all(Context context) {
		return Database.getDatabase(context).logDao().all();
	}

	public static List<Log> allPlain(Context context) {
		return Database.getDatabase(context).logDao().allPlain();
	}

	public static List<Log> allPlainNewest(Context context) {
		return Database.getDatabase(context).logDao().allPlainNewest();
	}

	public static void deleteAll(Context context) {
		Database.getDatabase(context).logDao().deleteAll();
	}

	public static Log get(int id, Context context) {
		return Database.getDatabase(context).logDao().get(id);
	}

	public static Log getUriNewest(String uri, String folderUri, Context context) {
		return Database.getDatabase(context).logDao().getUriNewest(uri, folderUri);
	}

	public static LiveData<List<Log>> search(String searchWord, Context context) {
		return Database.getDatabase(context).logDao().search(searchWord);
	}

	@PrimaryKey(autoGenerate = true)
	public int id;
	@NonNull
	public String label = "";
	@NonNull
	public String uri = "";
	@NonNull
	public String folderLabel = "";
	@NonNull
	public String folderUri = "";
	@NonNull
	public String md5 = "";
	@NonNull
	public String sha1 = "";
	@NonNull
	public String sha256 = "";

	@NonNull
	public String sha512 = "";

	public long size;

	public boolean modified = false;

	public boolean deleted = false;

	public boolean cantaccess = false;

	public long createdAt = System.currentTimeMillis();

	@NonNull
	String entryHash = "";

	public void delete(Context context) {
		Database.getDatabase(context).logDao().delete(this);
	}

	public void insert(Context context) {
		createdAt = System.currentTimeMillis();

		try {
			MessageDigest mdMd5 = MessageDigest.getInstance("MD5");
			MessageDigest mdSha1 = MessageDigest.getInstance("SHA-1");
			MessageDigest mdSha256 = MessageDigest.getInstance("SHA-256");
			MessageDigest mdSha512 = MessageDigest.getInstance("SHA-512");

			String data = label + uri + folderLabel + folderUri + createdAt + md5 + sha1 + sha256 + sha512 + size
					+ Prefs.getLong("log start time-stamp", 1);
			;
			mdMd5.update(data.getBytes());
			mdSha1.update(data.getBytes());
			mdSha256.update(data.getBytes());
			mdSha512.update(data.getBytes());

			entryHash = new BytesToString().bytesToString(mdMd5.digest());
			entryHash += new BytesToString().bytesToString(mdSha1.digest());
			entryHash += new BytesToString().bytesToString(mdSha256.digest());
			entryHash += new BytesToString().bytesToString(mdSha512.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		Database.getDatabase(context).logDao().insert(this);
	}

	public void update(Context context) {
		Database.getDatabase(context).logDao().update(this);
	}

	public boolean verify() {
		try {
			MessageDigest mdMd5 = MessageDigest.getInstance("MD5");
			MessageDigest mdSha1 = MessageDigest.getInstance("SHA-1");
			MessageDigest mdSha256 = MessageDigest.getInstance("SHA-256");
			MessageDigest mdSha512 = MessageDigest.getInstance("SHA-512");

			String data = label + uri + folderLabel + folderUri + createdAt + md5 + sha1 + sha256 + sha512 + size
					+ Prefs.getLong("log start time-stamp", 2);
			mdMd5.update(data.getBytes());
			mdSha1.update(data.getBytes());
			mdSha256.update(data.getBytes());
			mdSha512.update(data.getBytes());

			String checkHash = new BytesToString().bytesToString(mdMd5.digest());
			checkHash += new BytesToString().bytesToString(mdSha1.digest());
			checkHash += new BytesToString().bytesToString(mdSha256.digest());
			checkHash += new BytesToString().bytesToString(mdSha512.digest());
			return entryHash.equals(checkHash);
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
	}
}