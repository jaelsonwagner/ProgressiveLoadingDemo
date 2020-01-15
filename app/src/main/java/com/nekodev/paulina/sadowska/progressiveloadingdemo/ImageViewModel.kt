package com.nekodev.paulina.sadowska.progressiveloadingdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nekodev.paulina.sadowska.progressiveloadingdemo.fetcher.ImageFetcher
import com.nekodev.paulina.sadowska.progressiveloadingdemo.fetcher.data.BitmapResult
import com.nekodev.paulina.sadowska.progressiveloadingdemo.fetcher.data.BitmapWithQuality
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class ImageViewModel {

    companion object {
        private const val BASE_IMAGE_URL = "https://picsum.photos"
    }

    private val disposable = CompositeDisposable()
    private val fetcher = ImageFetcher(Picasso.get())

    private val _bitmapResult = MutableLiveData<BitmapResult>()

    val bitmapResult: LiveData<BitmapResult>
        get() = _bitmapResult

    fun loadImages(qualities: List<Int>) {
        _bitmapResult.value = BitmapResult.loading()

        disposable.add(
                fetcher.loadProgressively(BASE_IMAGE_URL, qualities)
                        .doOnSubscribe { resetImageView() }
                        .filter { getCurrentQuality() < it.quality }
                        .subscribeBy(
                                onNext = { applyImage(it) },
                                onComplete = {
                                    postErrorIfNotSufficientQuality()
                                }
                        ))
    }

    private fun resetImageView() {
        _bitmapResult.value = BitmapResult.loading()
    }

    private fun getCurrentQuality(): Int {
        return bitmapResult.value?.quality ?: -1
    }

    private fun applyImage(bitmap: BitmapWithQuality) {
        _bitmapResult.value = BitmapResult.success(bitmap)
    }

    private fun postErrorIfNotSufficientQuality() {
        if (getCurrentQuality() < 0) {
            _bitmapResult.value = BitmapResult.error()
        }
    }

    fun unSubscribe() {
        disposable.dispose()
    }
}