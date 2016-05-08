package com.my.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sh1r0.caffe_android_lib.CaffeMobile;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;


public class CameraActivity extends Activity implements CvCameraViewListener2 {

	private static final Logger logger = Logger.getLogger(CameraActivity.class.toString());
	
	private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

	static {
		System.loadLibrary("caffe");
		System.loadLibrary("caffe_jni");
	}

	CLAHE clahe;
	int left, top, right, bottom;
	private CameraBridgeViewBase mCameraView;
	private ListView listDetectedSigns;
	private RelativeLayout listRelativeLayout;
	private CascadeClassifier cascadeClassifier;
	private ArrayList<Sign> listSign;
	private Detector detector;
	private CaffeMobile caffeMobile;
	private boolean change;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                	mCameraView.enableView();
	                	detector = new Detector(CameraActivity.this);
						clahe = Imgproc.createCLAHE();
						caffeMobile = new CaffeMobile();
						caffeMobile.loadModel("/sdcard/caffe_mobile/traffic-signs/config_deploy.prototxt",
								"/sdcard/caffe_mobile/traffic-signs/_iter_10880.caffemodel");
						caffeMobile.setScale(0.00390625F);
	                    break;
	                default:
	                    super.onManagerConnected(status);
	                    break;
	            }
	        }
	    };
	private Mat mRgba;
	private Mat mGray;
	
		//detector = new Detector(CameraActivity.this);
	private void Initialze(){
		mCameraView = (CameraBridgeViewBase)findViewById(R.id.mCameraView);
		listDetectedSigns = (ListView)findViewById(R.id.listView1);
		listRelativeLayout = (RelativeLayout)findViewById(R.id.listViewLayout);
		mCameraView.setCvCameraViewListener(this);
		listRelativeLayout.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.camera_preview);
		Initialze();
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
	}
		
	@Override
    public void onResume() {
        super.onResume();
		if (!OpenCVLoader.initDebug()) {
            Log.d("fds", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("fds", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//TODO Auto-generated method stub
		mRgba = inputFrame.rgba();

		Size sizeRgba = mRgba.size();
		int rows = (int) sizeRgba.height;
		int cols = (int) sizeRgba.width;

		left = cols / 2;
		top = 0;
		right = cols;
		bottom = rows * 3 / 4;

		mGray = inputFrame.gray();
		Log.i("STEP","step");
		mGray = mGray.submat(top, bottom, left, right);


		//Imgproc.equalizeHist(mGray, mGray);
//		CLAHE clahe = Imgproc.createCLAHE();
//		clahe.apply(mGray, mGray);
		if (!change) {
			MatOfRect signs = new MatOfRect();
			listSign = new ArrayList<Sign>();
			Log.i("STEP", "DetectBegin");
			detector.Detect(mGray, signs, 1);
			Log.i("STEP", "DetectEnd");
			Rect[] prohibitionArray = signs.toArray();
			Draw(prohibitionArray);
			change = true;
		} else{
			MatOfRect signs = new MatOfRect();
        	detector.Detect(mGray, signs, 2);
        	Rect[] dangerArray = signs.toArray();
        	Draw(dangerArray);
			change = false;
		}


        //Core.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);*/

		Imgproc.rectangle(mRgba, new Point(left, top), new Point(right, bottom), FACE_RECT_COLOR, 2);
        return mRgba;
	}
	
	public void Draw(Rect[] facesArray){
//		if(facesArray.length<=0){
//        	runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					listRelativeLayout.setVisibility(View.GONE);
//				}
//			});
//
//        }
		//Imgproc.rectangle(mRgba,new Point(10, 10), new Point(100, 100), FACE_RECT_COLOR, 2);
        for (int i = 0; i <facesArray.length; i++){
        	final int ii = i;
			Log.e("RES", "start");
        	Mat subMat;
        	subMat = mGray.submat(facesArray[i]);
			Mat resizeMat = new Mat();
			Imgproc.resize(subMat, resizeMat, new Size(32, 32), 0, 0, Imgproc.INTER_CUBIC);

			clahe.apply(resizeMat, resizeMat);

			//Core.flip(resizeMat.t(), resizeMat, 0);
			//resizeMat = resizeMat.t();
			File fileDir = getImageFile();
			Imgcodecs.imwrite(fileDir.toString(), resizeMat);
			Log.e("RES", "start2");
			final int[] result = caffeMobile.predictImage(Uri.fromFile(fileDir).getPath());
			Log.e("RES", "result:" + Arrays.toString(result));
			final float[] rr = caffeMobile.getConfidenceScore(Uri.fromFile(fileDir).getPath());
			Log.e("RES", "rr:" + Arrays.toString(rr));
			Mat mat = Imgcodecs.imread(Uri.fromFile(fileDir).getPath(), -1);
			//Sign.myMap.put("image"+result[0], Utilities.convertMatToBitmap(mat));


			if (fileDir.exists()) {
				if (fileDir.delete()) {
					System.out.println("file Deleted :" + fileDir);
				} else {
					System.out.println("file not Deleted :" + fileDir);
				}
			}
			Log.e("RES", "stop");
			Point tl = facesArray[i].tl();
			tl.set(new double[]{tl.x + left, tl.y + top});
			Point br = facesArray[i].br();
			br.set(new double[]{br.x + left, br.y + top});

			Imgproc.rectangle(mRgba, tl, br, FACE_RECT_COLOR, 2);
        	if (rr[result[0]] > 0.9) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Sign sign = new Sign(result[0], rr[result[0]]);
						listSign.add(sign);
						listRelativeLayout.setVisibility(View.VISIBLE);
						itemAdapter adapter = new itemAdapter(listSign, CameraActivity.this);
						adapter.notifyDataSetChanged();
						listDetectedSigns.setAdapter(adapter);
					}
				});
			}
        	
        }
	}

	public File getImageFile() {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Signs");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("G", "failed to create directory");
				return null;
			}
		}

		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "testimage"  + ".jpg");
		//mediaFile = new File(mediaStorageDir.getPath() + File.separator + "testimage_20160425_175554.jpg");
		return mediaFile;
	}
}
