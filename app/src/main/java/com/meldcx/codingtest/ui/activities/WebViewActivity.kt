package com.meldcx.codingtest.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View.MeasureSpec
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.meldcx.codingtest.R
import com.meldcx.codingtest.data.models.HistoryEntity
import com.meldcx.codingtest.databinding.ActivityWebViewBinding
import com.meldcx.codingtest.service.appConstants.APP_DIRECTORY
import com.meldcx.codingtest.service.appConstants.DEFAULT_WEB_PROTOCAL
import com.meldcx.codingtest.service.appConstants.RETURN_INTENT_KEY
import com.meldcx.codingtest.service.appConstants.SAVED_IMAGE_PREFIX
import com.meldcx.codingtest.service.utils.gone
import com.meldcx.codingtest.service.utils.isValidUrl
import com.meldcx.codingtest.service.utils.visible
import com.meldcx.codingtest.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.content.ContentProviderResult

import android.provider.ContactsContract.CommonDataKinds.Email

import android.content.ContentProviderOperation

import android.provider.ContactsContract.CommonDataKinds.Phone

import android.provider.ContactsContract.CommonDataKinds.StructuredName





/*
* WebViewActivity uses ExperimentalCoroutinesApi
*
* INDICATE AS MAINACTIVITY
*
* Uses databinding layout the setContentview
* It has a webView and an edittext and 3 buttons
* Intially history and forward buttons are active.
* But when we put any text to the edittext the capture button appears.
* the by pressing the capture button we can captre webView image
* @AndroidEntryPoint annotation added, so you can inject any dependencies.
* */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WebViewActivity : BaseActivity() {

    private lateinit var binding:ActivityWebViewBinding
    private val viewModel by viewModels<MainViewModel>()
    private var existEntry:HistoryEntity? = null
    private var isPageIsLoading = false
    //permission to write external directory to store imatges
    private val neededPermissionsToWrite = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //permission to read info external directory to get stored imatges
    private val neededPermissionsToRead = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    //Permission result of write permission (Because requestPermissonResult is depricated)
    private val permissionResultToWrite = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value == true }
        if (granted) {
            lifecycleScope.launch(Dispatchers.IO){
                createBitmapAndSave()
            }
        }else{
            Toast.makeText(this,getString(R.string.permission_error),Toast.LENGTH_LONG).show()
        }
    }
    //Permission result of read permission (Because requestPermissonResult is depricated)
    private val permissionResultToRead = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value == true }
        if (granted) {
            historyIntentLaunch()
        }else{
            Toast.makeText(this,getString(R.string.permission_error),Toast.LENGTH_LONG).show()
        }
    }
    //Activity result catcher for startActivityResult  (startActivityResult is depricated)
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            try {
                val returnData = data?.extras?.getParcelable<HistoryEntity>(RETURN_INTENT_KEY)
                returnData?.url?.let {
                    existEntry = returnData
                    //Fetching and displaying the existing image from file
                    binding.apply {
                        existEntry?.imagePath?.let{
                            ivPrevSnapshot.setImageBitmap(getImageFromPath(it))
                            ivPrevSnapshot.visible()
                        }
                        includeSearchLayout.etUrlInput.setText(existEntry?.url)
                        tvNoItemLoaded.gone()
                        webView.visible()
                        btnCapture.visible()
                        webView.loadUrl(it)
                    }
                }
            }catch (e:Exception){}
        }
    }

    //override Abstruct function to initialize the view
    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        binding.root.post {
            buildUi()
            initListener()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
    }


    //Onclick listeners
    private fun initListener() {
        binding.ivForward.setOnClickListener {
            val urlText = binding.includeSearchLayout.etUrlInput.text.toString()
            if (urlText == DEFAULT_WEB_PROTOCAL || TextUtils.isEmpty(binding.includeSearchLayout.etUrlInput.text))
                Toast.makeText(this,getString(R.string.no_url_inputed),Toast.LENGTH_LONG).show()
            else{
                val validation = urlText.isValidUrl()
                if (validation.isNullOrEmpty()){
                    binding.apply {
                        webView.loadUrl(urlText)
                        tvNoItemLoaded.gone()
                        webView.visible()
                        btnCapture.visible()
                    }
                }else{
                    Toast.makeText(this,validation,Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnCapture.setOnClickListener {
            val unAcceptedPermissions = hasPermissions(neededPermissionsToWrite)
            if (unAcceptedPermissions.isNotEmpty())
                permissionResultToWrite.launch(unAcceptedPermissions.toTypedArray())
            else{
                lifecycleScope.launch(Dispatchers.IO){
                    if (isPageIsLoading)
                        withContext(Dispatchers.Main){Toast.makeText(this@WebViewActivity,getString(R.string.page_is_still_loading),Toast.LENGTH_LONG).show()}
                    else
                        createBitmapAndSave()
                }
            }
        }
        binding.btnHistory.setOnClickListener {
            /*val unAcceptedPermissions = hasPermissions(neededPermissionsToRead)
            if (unAcceptedPermissions.isNotEmpty())
                permissionResultToRead.launch(unAcceptedPermissions.toTypedArray())
            else{
                historyIntentLaunch()
            }*/
        }
    }

    private fun buildUi() {
        binding.apply {
            includeSearchLayout.etUrlInput.setText(DEFAULT_WEB_PROTOCAL)
        }
        initWebView()
        for (i in 0..1000){
            addContact("${i}th ")
        }
    }

    fun getRandomBillNo(): String? {
        val random = Random(10000)
        return (System.currentTimeMillis() + random.nextInt()).toString()
    }

    //Initalize webview
    private fun initWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress <= 90){
                        binding.ivPrevSnapshot.gone()
                    }else{
                        binding.ivPrevSnapshot.gone()
                    }
                }
            }
            webViewClient = object :WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    binding.includeSearchLayout.etUrlInput.setText(request?.url.toString())
                    viewModel.setCurrentUrl(request?.url.toString())
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isPageIsLoading = false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isPageIsLoading = true
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private suspend fun createBitmapAndSave(){
        val webViewBitmap = binding.webView.createBitmapFromWebView()
        val savedFile = File(handleDirectory(),SAVED_IMAGE_PREFIX+System.currentTimeMillis()+".png")
        try {
            FileOutputStream(savedFile).use { out ->
                webViewBitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
                viewModel.getCurrentUrl()?.let { HistoryEntity(imagePath = savedFile.absolutePath,url = it) }?.let {
                    withContext(Dispatchers.Main){
                        if (existEntry == null) {
                            viewModel.insertHistoryItem(it)
                            Toast.makeText(this@WebViewActivity,getString(R.string.insertion_success),Toast.LENGTH_LONG).show()
                        }else{
                            val updatedData = it
                            updatedData.id = existEntry!!.id
                            val oldFile = File(existEntry!!.imagePath)
                            if (oldFile.exists())
                                oldFile.delete()
                            viewModel.updateHistoryItem(it)
                            Toast.makeText(this@WebViewActivity,getString(R.string.update_success),Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun historyIntentLaunch() {
        val intent = Intent(this, HistoryActivity::class.java)
        resultLauncher.launch(intent)
    }


    //Didn't any better solution for creating bitmap from webview :)
    private fun WebView.createBitmapFromWebView():Bitmap?{
        measure(MeasureSpec.makeMeasureSpec(
            MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        layout(0, 0, measuredWidth,
            getMeasuredHeight());
        isDrawingCacheEnabled = true;
        buildDrawingCache();
        val bm = Bitmap.createBitmap(getMeasuredWidth(),
            measuredHeight, Bitmap.Config.ARGB_8888);

        val bigcanvas = Canvas(bm)
        val paint = Paint()
        val iHeight = bm.getHeight().toFloat()
        bigcanvas.drawBitmap(bm, 0f, iHeight, paint)
        draw(bigcanvas)
        return bm
    }

    private fun handleDirectory():File{
        val rootDir = getExternalFilesDir(null)
        val rootDirFile = File("${rootDir}/${APP_DIRECTORY}")
        if (!rootDirFile.exists())
            rootDirFile.mkdir()
        return rootDirFile
    }

    private fun getImageFromPath(path:String?):Bitmap?{
        val imgFile = File(path)
        return BitmapFactory.decodeFile(imgFile.absolutePath);
    }

    override fun getRootView() = binding.root

    private fun addContact(name:String) {
        val operationList = ArrayList<ContentProviderOperation>()
        operationList.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // first and last names
        operationList.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.GIVEN_NAME, "{$name} First")
                .withValue(StructuredName.FAMILY_NAME, "Last {$name}")
                .build()
        )
        operationList.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, "09876543210")
                .withValue(Phone.TYPE, Phone.TYPE_HOME)
                .build()
        )
        operationList.add(
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                .withValue(Email.DATA, "abc@xyz.com")
                .withValue(Email.TYPE, Email.TYPE_WORK)
                .build()
        )
        try {
            val results = contentResolver.applyBatch(ContactsContract.AUTHORITY, operationList)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}