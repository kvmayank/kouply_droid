package com.ourlittlegame.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ourlittlegame.utilities.FlushedInputStream;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;

public class BitmapDownloaderTask extends AsyncTask<String, Void, String> {
	private static boolean use_cache = true;
	private String url;
	private String filename;
	private int reqHeight;
	private int reqWidth;

	private final ArrayList<WeakReference<ImageView>> imageViewReferences = new ArrayList<WeakReference<ImageView>>();

	public BitmapDownloaderTask(ImageView imageView) {
		WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(
				imageView);
		imageViewReferences.add(imageViewReference);
	}

	public void addView(ImageView imageView) {
		WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(
				imageView);
		imageViewReferences.add(imageViewReference);
	}

	@Override
	// Actual download method, run in the task thread
	protected String doInBackground(String... params) {

		url = params[0];
		filename = params[1];
		reqHeight = Integer.parseInt(params[2]);
		reqWidth = Integer.parseInt(params[3]);

		if (use_cache) {
			File localImage = new File(filename);
			if (localImage.exists()) {
				return filename;
			}
		}

		if (downloadBitmap(url, filename))
			return filename;

		return null;
	}

	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(String bitmapFile) {
		if (isCancelled()) {
			return;
		}

		if (bitmapFile == null)
			return;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile, options);
		if (bitmap == null) {
			File localImage = new File(filename);
			if (localImage.exists())
				localImage.delete();
		}

		for (Iterator<WeakReference<ImageView>> it = imageViewReferences
				.iterator(); it.hasNext();) {
			WeakReference<ImageView> imageViewReference = it.next();
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					String tag = (String) imageView.getTag();
					if (url.equals(tag)) {
						imageView.setImageBitmap(bitmap);
						imageView.setVisibility(View.VISIBLE);
					}
				}
			}
		}

		ImageManager.clearTask(url);
	}

/*	static void storeBitmap(Bitmap bitmap, String filename) {
		try {
			File file = new File(filename);
			OutputStream fOut = new FileOutputStream(file);
			if (fOut != null) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);

				fOut.flush();
				fOut.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	static boolean downloadBitmap(String url, String filename) {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return false;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				FileOutputStream out = new FileOutputStream(filename);
				try {
					inputStream = entity.getContent();
					long contentLength = entity.getContentLength();

					int offset = 0;
					int chunk_size = 1024;
					byte[] data = new byte[chunk_size];
					while (offset < contentLength) {
						int bytesRead = inputStream.read(data, 0, data.length);
						if (bytesRead == -1)
							break;
						out.write(data, 0, bytesRead);
						offset += bytesRead;
					}
					inputStream.close();
					out.flush();
					out.close();

					if (offset != contentLength) {
						throw new IOException("Only read " + offset
								+ " bytes; Expected " + contentLength
								+ " bytes");
					}

					return true;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			MiscUtils.println("ImageDownloader"
					+ " Error while retrieving bitmap from " + url
					+ e.toString());
		} finally {
			if (client != null) {
				// client.close();
			}
		}
		return false;
	}

/*	static Bitmap downloadBitmapEx(String url) {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(new FlushedInputStream(inputStream));
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			MiscUtils.println("ImageDownloader"
					+ " Error while retrieving bitmap from " + url
					+ e.toString());
		} finally {
			if (client != null) {
				// client.close();
			}
		}
		return null;
	}*/

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
