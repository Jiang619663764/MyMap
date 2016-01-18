package com.jpmph.mybaidumap.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Context mContext;

	private float lastX;

	public MyOrientationListener(Context context) {
		mContext = context;
	}

	/**
	 * ����������
	 */
	public void startSonsor() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			// ��ȡ������
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if (mSensor != null) {
			// ע�ᴫ����
			mSensorManager.registerListener(this, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	/**
	 * �رմ�����
	 */
	public void stopSensor() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			float x = event.values[SensorManager.DATA_X];
			if (Math.abs(x - lastX) >= 1.0) {
				if(mOnOrientationListener!=null){
					mOnOrientationListener.onOrientationChange(lastX);
				}
			}
			lastX = x;
		}
	}

	private OnOrientationListener mOnOrientationListener;

	public void setOnOrientationListener(
			OnOrientationListener onOrientationListener) {

		mOnOrientationListener = onOrientationListener;
	}

	public interface OnOrientationListener {

		void onOrientationChange(float x);

	}
}
