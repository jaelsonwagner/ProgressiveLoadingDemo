package com.nekodev.paulina.sadowska.progressiveloadingdemo.fetcher

import com.nekodev.paulina.sadowska.progressiveloadingdemo.fetcher.data.BitmapWithQuality
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe

/**
 * Created by Paulina Sadowska on 03.07.2018.
 */
class ImageFetcherSingleSubscribe(private val picasso: Picasso,
                                  private val url: String,
                                  private val quality: Int) : SingleOnSubscribe<BitmapWithQuality> {

    private val runningTargets = mutableListOf<Target>()

    override fun subscribe(emitter: SingleEmitter<BitmapWithQuality>) {
        val target = CustomImageLoadTarget(emitter, quality) {
            removeTargetAndCancelRequest(it)
        }

        runningTargets.add(target)
        picasso.load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)     // avoiding cache to force reload
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)  // avoiding cache to force reload
                .into(target)
    }

    private fun removeTargetAndCancelRequest(target: Target) {
        picasso.cancelRequest(target)
        runningTargets.remove(target)
    }
}
