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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pixplicity.easyprefs.library.Prefs;

import io.github.folderlogs.tools.CPUWakeLock;
import io.github.folderlogs.tools.Scan;
import io.github.folderlogs.tools.Scheduler;
import io.github.folderlogs.ui.Notification;

public class ScanService extends Service {

	private CPUWakeLock mCPUWakeLock;
	private boolean oneMore = false;
	private boolean progress = false;

	public ScanService() {
	}

	private void closeMyself() {
		new Scheduler(this).scheduleScanService();
		mCPUWakeLock.releaseIfIsHeld();
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startForeground(1, new Notification(this).getScanProgress());
		mCPUWakeLock = new CPUWakeLock(this);
		mCPUWakeLock.acquire();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (progress) {
			oneMore = true;
			return super.onStartCommand(intent, flags, startId);
		}
		new Thread(() -> {
			try {
				progress = true;
				run();
				while (oneMore) {
					oneMore = false;
					run();
				}
				progress = false;
				closeMyself();
			} catch (Exception e) {
				new Notification(this).showServiceError(e.getMessage());
				progress = false;
				closeMyself();
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	private void run() {
		int change = new Scan(this).run();
		if (change > 0) {
			new Notification(this).showNewChange(change);
		} else if (Prefs.getBoolean("no change notification", false)) {
			new Notification(this).showNoChange();
		}
	}
}
