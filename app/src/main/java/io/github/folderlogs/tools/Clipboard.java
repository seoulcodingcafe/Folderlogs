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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Clipboard {

	Context mContext;

	public Clipboard(Context context) {
		mContext = context;
	}

	public void copy(String copy) {
		ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText(copy, copy);
		if (clipboardManager != null)
			clipboardManager.setPrimaryClip(clipData);
	}
}
