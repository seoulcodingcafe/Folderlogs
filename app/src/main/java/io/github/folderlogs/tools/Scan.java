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

package io.github.folderlogs.tools;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import io.github.folderlogs.db.Folder;
import io.github.folderlogs.db.Log;

public class Scan {
	Context mContext;

	public Scan(Context context) {
		mContext = context;
	}

	public int run() {
		int change = 0;
		change += runCheckLogExistance();
		List<Folder> folder = Folder.allPlain(mContext);
		for (Folder f : folder) {
			change += runCheckFolder(f.folderLabel, Uri.parse(f.folderUri));
		}
		return change;
	}

	private int runCheckFile(String parent, DocumentFile file, DocumentFile folder) {
		boolean cantaccess = false;
		String md5 = "???";
		String sha1 = "???";
		String sha256 = "???";
		String sha512 = "???";
		long sumRead = 0;
		try {
			InputStream is = mContext.getContentResolver().openInputStream(file.getUri());
			MessageDigest mdMd5 = MessageDigest.getInstance("MD5");
			MessageDigest mdSha1 = MessageDigest.getInstance("SHA-1");
			MessageDigest mdSha256 = MessageDigest.getInstance("SHA-256");
			MessageDigest mdSha512 = MessageDigest.getInstance("SHA-512");
			byte[] bytes = new byte[1024];
			int sizeRead;
			do {
				sizeRead = Objects.requireNonNull(is).read(bytes);
				if (sizeRead > 0) {
					mdMd5.update(bytes, 0, sizeRead);
					mdSha1.update(bytes, 0, sizeRead);
					mdSha256.update(bytes, 0, sizeRead);
					mdSha512.update(bytes, 0, sizeRead);
					sumRead += sizeRead;
				}
			} while (sizeRead != -1);

			md5 = new BytesToString().bytesToString(mdMd5.digest());
			sha1 = new BytesToString().bytesToString(mdSha1.digest());
			sha256 = new BytesToString().bytesToString(mdSha256.digest());
			sha512 = new BytesToString().bytesToString(mdSha512.digest());
		} catch (IOException | NoSuchAlgorithmException | NullPointerException ex) {
			cantaccess = true;
		}

		Log log = Log.getUriNewest(file.getUri().toString(), folder.getUri().toString(), mContext);
		boolean modified = false;
		if (log != null && !log.deleted) {
			boolean notchange;
			notchange = md5.equals(log.md5) && !md5.equals("???");
			notchange = notchange && (sha1.equals(log.sha1) && !sha1.equals("???"));
			notchange = notchange && (sha256.equals(log.sha256) && !sha256.equals("???"));
			notchange = notchange && (sha512.equals(log.sha512) && !sha512.equals("???"));
			notchange = notchange && log.size == sumRead;
			if (notchange && log.verify())
				return 0;
			else
				modified = true;
		}

		log = new Log();
		log.folderLabel = parent;
		log.folderUri = folder.getUri().toString();

		try {
			log.label = Objects.requireNonNull(file.getName());
		} catch (NullPointerException n) {
			log.label = file.getUri().toString();
		}
		log.uri = file.getUri().toString();
		log.md5 = md5;
		log.sha1 = sha1;
		log.sha256 = sha256;
		log.sha512 = sha512;
		log.size = sumRead;
		log.cantaccess = cantaccess;
		log.modified = modified;
		log.insert(mContext);
		return 1;
	}

	private int runCheckFolder(String parent, DocumentFile doc) {
		int change = 0;
		for (DocumentFile file : doc.listFiles()) {
			if (file.isFile())
				change += runCheckFile(parent, file, doc);
			else
				change += runCheckFolder(parent + "/" + file.getName(), file);
		}
		return change;
	}

	private int runCheckFolder(String parent, Uri uri) {
		DocumentFile doc = DocumentFile.fromTreeUri(mContext, uri);
		if (doc == null)
			return 0;
		return runCheckFolder(parent, doc);
	}

	public int runCheckLogExistance() {
		int change = 0;
		List<Log> logs = Log.allPlainNewest(mContext);
		for (Log log : logs) {
			if (log.deleted)
				continue;
			DocumentFile doc = DocumentFile.fromSingleUri(mContext, Uri.parse(log.uri));
			if (doc == null || !doc.exists() || !doc.isFile()) {
				Log newlog = new Log();
				newlog.deleted = true;
				newlog.cantaccess = log.cantaccess;
				newlog.folderLabel = log.folderLabel;
				newlog.folderUri = log.folderUri;
				newlog.size = log.size;
				newlog.sha512 = log.sha512;
				newlog.sha256 = log.sha256;
				newlog.sha1 = log.sha1;
				newlog.label = log.label;
				newlog.uri = log.uri;
				newlog.insert(mContext);
				change += 1;
			}
		}
		return change;
	}

}
