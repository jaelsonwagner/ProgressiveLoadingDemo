package com.nekodev.paulina.sadowska.progressiveloadingdemo

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class ImageLoadingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler(RxJavaErrorHandler())

        // open chrome://inspect/#devices on Chrome browse, then click on "inspect" link.
        Stetho.initializeWithDefaults(this)

        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(StethoInterceptor()) // add network interceptor
                .build()

        val instance = Picasso.Builder(this)
                .downloader(OkHttp3Downloader(client))  // custom downloader
                .loggingEnabled(true)
                .build()

        Picasso.setSingletonInstance(instance)
    }
}
