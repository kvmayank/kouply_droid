package com.ourlittlegame.utilities;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.ourlittlegame.tasks.BitmapDownloaderTask;

public class ImageManager {
	private static HashMap<String, BitmapDownloaderTask> activeTasks = new HashMap<String, BitmapDownloaderTask>();
	
	private static boolean scheduleDownloadTask(String url, String filename,
			ImageView imageView, int reqHeight, int reqWidth) {
		imageView.setTag(url);
		if (activeTasks.containsKey(url)) {
			BitmapDownloaderTask task = activeTasks.get(url);
			if (task != null) {
				task.addView(imageView);
				return false;
			} else {
				clearTask(url);
			}
		}

		// MiscUtils.println("Downloading image from "+url);
		BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		try {
			task.execute(url, filename, reqHeight+"", reqWidth+"");
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
			return false;
		}

		activeTasks.put(url, task);
		return true;
	}

	public static void clearTask(String url) {
		activeTasks.remove(url);
	}

	/*private static void download(String url, ImageView imageView) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute(url);
	}*/

	public static void showPicture(int user_id, String url, 
			ImageView imageView, String appFolder) {
		
		if (MiscUtils.removeNull(url).length() == 0)
			return;
		
		imageView.setTag(user_id + "");
		File fFolder = new File(appFolder);
		fFolder.mkdirs();
		if (fFolder.exists()) {
			String filename = appFolder + Encryption.md5("" + user_id) + ".jpg";
			//System.out.println("Height/Width: "+imageView.getHeight() + "/" + imageView.getWidth());
			scheduleDownloadTask(url, filename, imageView, 300, 300);
		}
	}
	
	public static void showPicture(String url, 
			ImageView imageView, String appFolder) {
		imageView.setTag(url);

		if (MiscUtils.removeNull(url).length() == 0)
			return;
		
		File fFolder = new File(appFolder);
		fFolder.mkdirs();
		if (fFolder.exists()) {
			String filename = appFolder + Encryption.md5(url) + ".jpg";
			//System.out.println("Height/Width: "+imageView.getHeight() + "/" + imageView.getWidth());
			scheduleDownloadTask(url, filename, imageView, 300, 300);
		}
	}
}