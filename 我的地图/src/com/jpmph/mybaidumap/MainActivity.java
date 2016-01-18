package com.jpmph.mybaidumap;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.jpmph.mybaidumap.bean.SearchBean;
import com.jpmph.mybaidumap.listener.MyOrientationListener;
import com.jpmph.mybaidumap.listener.MyOrientationListener.OnOrientationListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private BaiduMap mBaiduMap;
	private MapView mMapView;

	// 定位相关
	private boolean isFirstIn = true;
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private BitmapDescriptor mBitmapDescriptor;// �Զ�λͼ��

	// 定位时的经纬度
	private double mLatitude;
	private double mLongitude;

	//传感器控件
	private MyOrientationListener mOrientationListener;
	private float mCurrentX;

	// 搜索查找poi
	private PoiSearch mPoiSearch;
	private PoiNearbySearchOption mNearbySearch;
	private PoiCitySearchOption mCitySearch;
	private EditText mEdtSearch;
	private Button mBtnSearch;

	private List<SearchBean> list;
	private SearchBean mSearchBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		// 初始化控件(MapView,BaiduMap)
		initView();
		// 初始化定位
		initLocation();

	}

	/**
	 *初始化控件
	 */
	private void initView() {

		mEdtSearch = (EditText) findViewById(R.id.edt_search);
		mBtnSearch = (Button) findViewById(R.id.btn_search);

		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15f);
		// poi实例化
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch
				.setOnGetPoiSearchResultListener(mOnGetPoiSearchResultListener);
		mBtnSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_search:
			poiNearbySearch();
			break;

		default:
			break;
		}

	}

	/**
	 * 周边搜索
	 */
	private void poiNearbySearch() {
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		mNearbySearch = new PoiNearbySearchOption() {
		}//
		.keyword(mEdtSearch.getText().toString()).location(latLng)//
				.pageCapacity(15)//
				.pageNum(0)//
				.radius(10000)//
				.sortType(PoiSortType.distance_from_near_to_far);
		mPoiSearch.searchNearby(mNearbySearch);
	}

	/**
	 * 城市搜索
	 */
	private void poiCitySearch() {
		mCitySearch = new PoiCitySearchOption() {
		}//
		.city("")//
				.keyword(mEdtSearch.getText().toString())//
				.pageCapacity(15)//
				.pageNum(0);

		mPoiSearch.searchInCity(mCitySearch);
	}

	OnGetPoiSearchResultListener mOnGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult result) {
			list = new ArrayList<SearchBean>();
			for (int i = 0; i < result.getAllPoi().size(); i++) {
				mSearchBean = new SearchBean();
				mSearchBean.setAddress(result.getAllPoi().get(i).address);
				mSearchBean
						.setLatitude(result.getAllPoi().get(i).location.latitude);
				mSearchBean
						.setLontitude(result.getAllPoi().get(i).location.longitude);
				mSearchBean.setName(result.getAllPoi().get(i).name);
				list.add(mSearchBean);
			}
			addMarker(list);
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 添加覆盖物
	 */
	private void addMarker(List<SearchBean> info) {
		mBaiduMap.clear();
		Marker marker = null;
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		BitmapDescriptor markerIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_mark99);

		for (SearchBean infos : info) {
			latLng = new LatLng(infos.getLatitude(), infos.getLontitude());
			overlayOptions = new MarkerOptions()//
					.position(latLng)//
					.icon(markerIcon)//
					.zIndex(5);
			marker=(Marker) mBaiduMap.addOverlay(overlayOptions);
			Bundle bundle=new Bundle();
			bundle.putSerializable("info", infos);
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	/**
	 * 初始化定位
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(MainActivity.this);
		mLocationListener = new MyLocationListener();
		//注册
		mLocationClient.registerLocationListener(mLocationListener);
		// 参数
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);

		//定位的图标
		mBitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.marker);

		mOrientationListener = new MyOrientationListener(this);
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {

					@Override
					public void onOrientationChange(float x) {
						// TODO Auto-generated method stub
						mCurrentX = x;
					}
				});

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			//赋值经纬度
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			// 自定义定位图标
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.NORMAL, true, mBitmapDescriptor);
			mBaiduMap.setMyLocationConfigeration(config);

			if (isFirstIn) {
				LatLng latlng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.id_map_common:
			// 设置普通视图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_map_site:
			//设置卫星视图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_map_traffic:

			// 设置实时交通
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通(on)");
			}
			break;
		case R.id.id_map_mylocation:
			//定义我的位置
			LatLng latLng = new LatLng(mLatitude, mLongitude);
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
			mBaiduMap.animateMapStatus(msu);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		mOrientationListener.startSonsor();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//关闭定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		//关闭方向传感器
		mOrientationListener.stopSensor();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

}
