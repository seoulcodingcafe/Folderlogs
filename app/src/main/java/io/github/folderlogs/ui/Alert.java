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

package io.github.folderlogs.ui;

import android.content.Context;

import com.jaredrummler.cyanea.Cyanea;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.NumberFormat;
import java.util.Date;

import id.ionbit.ionalert.IonAlert;
import io.github.folderlogs.R;
import io.github.folderlogs.db.Folder;
import io.github.folderlogs.db.Log;
import io.github.folderlogs.tools.Clipboard;
import io.github.folderlogs.tools.ConvertStringHTML;

public class Alert {

	private Context mContext;

	public Alert(Context context) {
		mContext = context;
	}

	public void copied() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE).setTitleText(mContext.getString(R.string.copied)).show();
	}

	private void copyHash(Log log) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(mContext.getString(R.string.select_hash))
				.setConfirmText(mContext.getString(R.string.sha)).setCancelText(mContext.getString(R.string.md5))
				.setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					copySHA(log);
				}).setCancelClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					new Clipboard(mContext).copy(log.md5);
					copied();
				}).show();
	}

	private void copyMoreSHA(Log log) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(mContext.getString(R.string.select_sha))
				.setConfirmText(mContext.getString(R.string.sha1)).setCancelText(mContext.getString(R.string.sha512))
				.setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					new Clipboard(mContext).copy(log.sha1);
					copied();
				}).setCancelClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					new Clipboard(mContext).copy(log.sha512);
					copied();
				}).show();
	}

	private void copySHA(Log log) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(mContext.getString(R.string.select_sha))
				.setConfirmText(mContext.getString(R.string.sha256)).setCancelText(mContext.getString(R.string.more))
				.setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					new Clipboard(mContext).copy(log.sha256);
					copied();
				}).setCancelClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					copyMoreSHA(log);
				}).show();
	}

	public void deleted() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE).setTitleText(mContext.getString(R.string.deleted)).show();
	}

	public void deleteFolder(Folder folder) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.WARNING_TYPE).setTitleText(mContext.getString(R.string.are_you_sure))
				.setConfirmText(mContext.getString(R.string.yes_remove_folder)).setConfirmClickListener(ionAlert -> {
					folder.delete(mContext);
					ionAlert.dismissWithAnimation();
					removed();
				}).setCancelText(mContext.getString(R.string.no_dont_remove_folder)).show();
	}

	public void deleteLogs() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.WARNING_TYPE).setTitleText(mContext.getString(R.string.are_you_sure))
				.setConfirmText(mContext.getString(R.string.yes_delete_logs)).setConfirmClickListener(ionAlert -> {
					Log.deleteAll(mContext);
					Prefs.putLong("log start time-stamp", System.currentTimeMillis());
					ionAlert.dismissWithAnimation();
					deleted();
				}).setCancelText(mContext.getString(R.string.no_dont_delete_logs)).show();
	}

	public void detectedChanges(int count) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		IonAlert ionAlert;
		if (count == 0)
			ionAlert = new IonAlert(mContext, IonAlert.SUCCESS_TYPE);
		else
			ionAlert = new IonAlert(mContext, IonAlert.WARNING_TYPE);
		ionAlert.setTitleText(mContext.getString(R.string.detected_changes) + ": " + count);
		ionAlert.show();
	}

	public void error(String error) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.ERROR_TYPE).setTitleText(mContext.getString(R.string.error))
				.setContentText(error).show();
	}

	public IonAlert loading() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		return new IonAlert(mContext, IonAlert.PROGRESS_TYPE).setTitleText(mContext.getString(R.string.loading))
				.setSpinKit("FadingCircle")
				.setSpinColor("#" + Integer.toHexString(Cyanea.getInstance().getPrimaryDark()));
	}

	public void openFolder(Folder folder) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(folder.folderLabel)
				.setContentText(new ConvertStringHTML()
						.convertBackslashN(mContext.getString(R.string.folder_uri) + ":\n" + folder.folderUri))
				.setConfirmText(mContext.getString(R.string.remove_folder))
				.setCancelText(mContext.getString(R.string.Close)).setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					deleteFolder(folder);
				}).show();
	}

	public void openLog(Log log) {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		String contentText;
		contentText = mContext.getString(R.string.date) + ": " + new Date(log.createdAt).toLocaleString() + "\n";
		contentText += mContext.getString(R.string.file) + ": " + log.label + "\n";
		contentText += mContext.getString(R.string.folder) + ": " + log.folderLabel + "\n";
		contentText += mContext.getString(R.string.size) + ": " + NumberFormat.getInstance().format(log.size) + " "
				+ mContext.getString(R.string.bytes) + "\n" + "\n";
		contentText += mContext.getString(R.string.md5) + ": " + log.md5 + "\n";
		contentText += mContext.getString(R.string.sha1) + ": " + log.sha1 + "\n";
		contentText += mContext.getString(R.string.sha256) + ": " + log.sha256 + "\n";
		contentText += mContext.getString(R.string.sha512) + ": " + log.sha512 + "\n";
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(log.label)
				.setContentText(new ConvertStringHTML().convertBackslashN(contentText))
				.setConfirmText(mContext.getString(R.string.copy_hash))
				.setCancelText(mContext.getString(R.string.Close)).setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					copyHash(log);
				}).show();
	}

	public void removed() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE).setTitleText(mContext.getString(R.string.removed)).show();
	}

	public IonAlert scanning() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		return new IonAlert(mContext, IonAlert.PROGRESS_TYPE).setTitleText(mContext.getString(R.string.scanning))
				.setSpinKit("ThreeBounce")
				.setSpinColor("#" + Integer.toHexString(Cyanea.getInstance().getPrimaryDark()));
	}
}
