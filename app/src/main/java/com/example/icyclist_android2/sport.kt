package com.example.icyclist_android2

import android.Manifest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
//import com.amap.api.col.`3sl`.it
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 这是运动界面，实现了高德地图的展示，用户蓝点定位，模拟用户骑行并后处理绘制骑行轨迹，开始跑步，长按暂停结束跑步
 * 存储跑步数据：时间，用户名，路径坐标到后端数据库
 * 实现地图视图截图，保存截图到本地，以便于在“发现”社交圈子中分享自己的骑行
 */
class sport : AppCompatActivity(),AMapLocationListener, LocationSource {

    // 请求权限码
    private companion object {
        const val REQUEST_PERMISSIONS = 9527
        const val TAG = "sport"
    }


    private lateinit var timerChronometer: Chronometer
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var continueButton: Button
    private lateinit var usernameEditText:EditText
    private var isPaused = false
    private lateinit var viewRideButton:Button
    private lateinit var captureButton:Button

    private lateinit var mapView: MapView
    private lateinit var aMap: AMap
    private var mListener: LocationSource.OnLocationChangedListener? = null


    // 声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null
    // 声明AMapLocationClientOption对象
    private var mLocationOption: AMapLocationClientOption? = null

    // 用于存储骑行路径的经纬度点集合
    private val ridePath = mutableListOf<LatLng>()
    // Polyline对象，用于在地图上绘制路线
    private var ridePolyline: Polyline? = null
    private var marker: Marker? = null
    private var isMockingLocation = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mockLocationRunnable: Runnable

    // 添加总公里数变量
    private var totalDistance: Float = 0f










    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sport)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        timerChronometer = findViewById(R.id.timerChronometer)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        continueButton = findViewById(R.id.continueButton)
    //    usernameEditText = findViewById(R.id.usernameEditText)
        // 初始化按钮
        viewRideButton = findViewById(R.id.viewRideButton)
        // 初始化地图
        initMap(savedInstanceState)

        // 初始化定位
        initLocation()

        // 检查Android版本
        checkingAndroidVersion()

        // 假设有一个按钮用来截图
        captureButton = findViewById(R.id.captureButton)


        startButton.setOnClickListener {
            if (!isPaused) {
                startTimer()
            }
            // 开始记录骑行
            startMockingLocation()

        }

        pauseButton.setOnClickListener {
            togglePauseResumeTimer()
            // 停止记录骑行并绘制路线
            stopMockingLocation()
            drawRidePath()
        }

        pauseButton.setOnLongClickListener {
            showConfirmDialog()
            true // 返回true表示点击事件已消费
        }

        continueButton.setOnClickListener {
            resumeTimer()
        }

        //跳转按钮
        val spbutton1 = findViewById<Button>(R.id.spbutton1)
        val spbutton3 = findViewById<Button>(R.id.spbutton3)
        val viewRideButton=findViewById<Button>(R.id.viewRideButton)
        spbutton1.setOnClickListener {
            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)
        }
        spbutton3.setOnClickListener {
            val intent2 = Intent(this,discovery::class.java)
            startActivity(intent2)
        }
        viewRideButton.setOnClickListener {
            val intent3 = Intent(this,ViewRideActivity::class.java)
            startActivity(intent3)
        }
    }
    private fun initMap(savedInstanceState: Bundle?) {
        mapView = findViewById(R.id.map)
        // 在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState)
        // 初始化地图控制器对象
        aMap = mapView.map
        // 设置定位监听
        aMap?.setLocationSource(this)
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap?.isMyLocationEnabled = true
    }

    private fun initLocation() {
        // 隐私合规接口
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)

        // 初始化定位
        mLocationClient = AMapLocationClient(applicationContext)
        mLocationClient?.let {
            // 设置定位回调监听
            it.setLocationListener(this)
            // 初始化AMapLocationClientOption对象
            mLocationOption = AMapLocationClientOption().apply {
                // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
                locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                // 获取最近3s内精度最高的一次定位结果：
                isOnceLocationLatest = true
                // 设置是否返回地址信息（默认返回地址信息）
                isNeedAddress = true
                // 设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
                httpTimeOut = 20000
                // 关闭缓存机制，高精度定位会产生缓存。
                isLocationCacheEnable = false
            }
            // 给定位客户端对象设置定位参数
            it.setLocationOption(mLocationOption)
        }
    }

    /**
     * 检查Android版本
     */
    private fun checkingAndroidVersion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Android6.0及以上先获取权限再定位
            requestPermission()
        } else {
            // Android6.0以下直接定位
            // 启动定位
            mLocationClient?.startLocation()
        }
    }
    private fun startMockingLocation() {
        isMockingLocation = true
        mockLocationRunnable = Runnable {
            if (isMockingLocation) {
                // 更新位置
                val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = 30.316389 // 更新纬度
                    longitude = 120.375278 + ridePath.size * 0.00001 // 更新经度
                    time = System.currentTimeMillis()
                }
                ridePath.add(LatLng(mockLocation.latitude, mockLocation.longitude))
                updateMarker(mockLocation)

                // 计算距离
                calculateDistance()

                handler.postDelayed(mockLocationRunnable, 1000) // 一秒后再次运行
            }
        }
        handler.post(mockLocationRunnable)
    }

    //计算骑行距离
    private fun calculateDistance() {
        if (ridePath.size < 2) return // 如果没有足够的点，则返回
        val startLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = ridePath[ridePath.size - 2].latitude
            longitude = ridePath[ridePath.size - 2].longitude
        }
        val endLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = ridePath[ridePath.size - 1].latitude
            longitude = ridePath[ridePath.size - 1].longitude
        }

        // 计算距离并累加
        val distance = startLocation.distanceTo(endLocation) // 单位为米
        totalDistance += distance // 累加到总距离
    }


    private fun stopMockingLocation() {
        isMockingLocation = false
        handler.removeCallbacks(mockLocationRunnable)
    }

    private fun drawRidePath() {
        if (ridePath.isNotEmpty()) {
            ridePolyline?.remove()
            ridePolyline = aMap?.addPolyline(
                PolylineOptions()
                .addAll(ridePath)
                .color(Color.BLUE)
                .width(10f))
        }
    }

    private fun updateMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (marker == null) {
            marker = aMap?.addMarker(com.amap.api.maps.model.MarkerOptions().position(latLng).title("Current Position"))
        } else {
            marker?.position = latLng
        }
        aMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            // 已获得权限，可以定位
            Log.d(TAG, "已获得权限，可以定位啦！")
            // 启动定位
            mLocationClient?.startLocation()
        } else {
            // 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, *permissions)
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(aMapLocation: AMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.errorCode == 0) {
                // 地址
                val address = aMapLocation.address
                val latitude = aMapLocation.latitude
                val longitude = aMapLocation.longitude

                val stringBuilder = StringBuilder()
                stringBuilder.append("纬度：$latitude\n")
                stringBuilder.append("经度：$longitude\n")
                stringBuilder.append("地址：$address\n")
                Log.d(TAG, stringBuilder.toString())
                showMsg(address)

                mLocationClient?.stopLocation()

                mListener?.onLocationChanged(aMapLocation)

            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.errorCode + ", errInfo:" + aMapLocation.errorInfo)
            }
        }
    }

    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        mListener = onLocationChangedListener
        mLocationClient?.startLocation() // 启动定位
    }

    /**
     * 停止定位
     */
    override fun deactivate() {
        mListener = null
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        mLocationClient = null
    }

    private fun startTimer() {
        timerChronometer.start()
        isPaused = false
    }

    private fun togglePauseResumeTimer() {
        if (!isPaused) {
            pauseTimer()
        } else {
            resumeTimer()
        }
    }

    private fun pauseTimer() {
        timerChronometer.stop()
        isPaused = true
    }

    private fun resumeTimer() {
        timerChronometer.start()
        isPaused = false
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this@sport)
            .setMessage("确定要结束本次骑行吗？")
            .setPositiveButton("确定") { dialog, which ->
                resetTimer()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun resetTimer() {
        val totalDuration = (SystemClock.elapsedRealtime() - timerChronometer.base) / 1000 // 转为秒
        timerChronometer.base = SystemClock.elapsedRealtime()
        timerChronometer.stop()

        // 获取用户名
      //  val username = usernameEditText.text.toString()
        // 从 SharedPreferences 获取用户名
        val username = getUsername() // 调用方法获取用户名

        // 将路径坐标转换为字符串
        val pathCoordinates = ridePath.joinToString(";") { "${it.latitude},${it.longitude}" }

        // 创建 ExerciseData 实例
        val exerciseData = ExerciseData(username, totalDuration.toInt(), pathCoordinates)

        // 发送运动数据到后端
        sendExerciseData(exerciseData)

        // 共享骑行路径
        shareRidePath()

        // 显示总公里数
        showMsg("本次骑行距离: ${totalDistance / 1000} 公里") // 转为公里

        captureButton.setOnClickListener {
            captureMapSnapshot()
        }
        // 跳转到 insert活动
        insert(totalDuration)

    }
    private fun insert(totalDuration:Long) {
        val intent = Intent(this, insert::class.java)
        // 将时长转换为时:分:秒格式
        val durationString = String.format("%02d:%02d:%02d",
            totalDuration / 3600,
            (totalDuration % 3600) / 60,
            totalDuration % 60
        )

        val formamattedDistance= String.format("%.2f",totalDistance / 1000)
        // 传递总公里数和总时间
        intent.putExtra("RIDE_DISTANCE",formamattedDistance ) // 公里
        intent.putExtra("RIDE_DURATION", durationString) // 时:分:秒
        startActivity(intent)
    }
    // 获取用户名的方法
    private fun getUsername(): String {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("username", "") ?: ""
    }

    private fun sendExerciseData(exerciseData: ExerciseData) {
        RetrofitClient.exerciseApi.saveExerciseData(exerciseData).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@sport, "数据保存成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@sport, "数据保存失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@sport, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun shareRidePath() {
      //  val username = usernameEditText.text.toString()
        // 从 SharedPreferences 获取用户名
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: "未知用户"
        val pathCoordinates = ridePath.joinToString(";") { "${it.latitude},${it.longitude}" }
        val shareText = "我刚刚骑行的路线：\n用户名：$username\n路径坐标：$pathCoordinates"

        // 使用 ShareCompat 进行分享
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle("分享骑行路线")
            .setText(shareText)
            .startChooser()
    }

    //获取地图视图的截图
    private fun captureMapSnapshot() {
        Log.d("MapViewStatus", "Visibility: ${mapView.visibility}, Width: ${mapView.width}, Height: ${mapView.height}")

        aMap?.let { map ->
            map.getMapScreenShot(object : AMap.OnMapScreenShotListener {
                override fun onMapScreenShot(bitmap: Bitmap?) {
                    if (bitmap != null) {
                        // 保存bitmap为文件
                        saveBitmapToFile(bitmap)
                    } else {
                        Toast.makeText(this@sport, "截图失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onMapScreenShot(bitmap: Bitmap?, status: Int) {
                    // 处理截图失败的情况
                }
            })
        }
    }


    //保存截图到本地
    private fun saveBitmapToFile(bitmap: Bitmap) {
        val filename = "ride_snapshot_${System.currentTimeMillis()}.png"
        val file = File(getExternalFilesDir(null), filename)

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // 保存为PNG格式
                Toast.makeText(this, "截图已保存到: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroy() {
        super.onDestroy()

        // 销毁定位客户端，同时销毁本地定位服务。
        mLocationClient?.onDestroy()
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState)
    }
}