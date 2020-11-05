package com.webserveis.mysubscriptions.common


import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.webserveis.mysubscriptions.BuildConfig
import java.net.MalformedURLException
import java.net.URL

class AdMobHelper(private val context: Activity, private val adView: AdView) {

    private val testDeviceIds: List<String> = listOf(
        "change-this",
        "change-this"
    )
    private var adRequest: AdRequest? = null

    //private lateinit var consentInformation: ConsentInformation
    private lateinit var form: ConsentForm


    init {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE)
                .build()
        )
        MobileAds.initialize(context) {
            Log.d(TAG, "Ads initialize ${it.adapterStatusMap}")
            checkForConsent()
        }


        //initializeAds(adView, true)

    }

    private fun checkForConsent() {
        Log.d(TAG, "checkForConsent: ")

        val consentInformation = ConsentInformation.getInstance(context)
        consentInformation.addTestDevice(testDeviceIds.first())

        val publisherIds = arrayOf(BuildConfig.MY_ADMOB_PUBLISHER_ID)
        Log.d(TAG, "publisherIds:" + BuildConfig.MY_ADMOB_PUBLISHER_ID)

        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                // User's consent status successfully updated.
                when (consentStatus) {
                    ConsentStatus.PERSONALIZED -> {
                        Log.d(TAG, "Showing Personalized ads")
                        //showPersonalizedAds()
                        initializeAds(adView, true)
                    }
                    ConsentStatus.NON_PERSONALIZED -> {
                        Log.d(TAG, "Showing Non-Personalized ads")
                        //showNonPersonalizedAds()
                        initializeAds(adView, false)
                    }

                    ConsentStatus.UNKNOWN -> {
                        Log.d(TAG, "Requesting Consent")
                        if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown) {
                            requestConsent()
                        } else {
                            initializeAds(adView, true)
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                // User's consent status failed to update.
                Log.e(TAG, "onFailedToUpdateConsentInfo: $errorDescription")
            }
        })
    }

    private fun requestConsent() {
        var privacyUrl: URL? = null
        try {
            privacyUrl = URL("https://your.privacy.url/")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        form = ConsentForm.Builder(context, privacyUrl)
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    // Consent form loaded successfully.
                    Log.d(TAG, "Requesting Consent: onConsentFormLoaded")
                    showConsentForm()
                }

                override fun onConsentFormOpened() {
                    // Consent form was displayed.
                    Log.d(TAG, "Requesting Consent: onConsentFormOpened")
                }

                override fun onConsentFormClosed(
                    consentStatus: ConsentStatus, userPrefersAdFree: Boolean
                ) {
                    Log.d(TAG, "Requesting Consent: onConsentFormClosed")
                    if (userPrefersAdFree) {
                        // Buy or Subscribe
                        Log.d(TAG, "Requesting Consent: User prefers AdFree")
                    } else {
                        Log.d(TAG, "Requesting Consent: Requesting consent again")
                        when (consentStatus) {
                            ConsentStatus.PERSONALIZED -> initializeAds(adView, true)
                            ConsentStatus.NON_PERSONALIZED -> initializeAds(adView, false)
                            ConsentStatus.UNKNOWN -> initializeAds(adView, true)
                        }
                    }
                    // Consent form was closed.
                }

                override fun onConsentFormError(errorDescription: String) {
                    Log.d(TAG, "Requesting Consent: onConsentFormError. Error - $errorDescription")
                    // Consent form error.
                }
            })
            .withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
            //.withAdFreeOption()
            .build()
        form.load()
    }

    private fun showConsentForm() {
        Log.d(TAG, "Showing consent form")
        form.show()
    }

    fun initializeAds(adView: AdView, isPersonalizedAds: Boolean = true) {
        Log.d(TAG, "initializeAds() called with: adView = [$adView], isPersonalizedAds = [$isPersonalizedAds]")
        adRequest = if (isPersonalizedAds) {
            AdRequest.Builder().build() //personalized ads
        } else {
            //none personalized ads
            val extras = Bundle()
            extras.putString("npa", "1")

            AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        }

        // load the request into your adView

        adView.loadAd(adRequest)
        //adView.adSize = AdSize.SMART_BANNER
    }

    fun isTestDevice(): Boolean {
        return adRequest?.isTestDevice(context) ?: false
    }


    companion object {
        private val TAG = AdMobHelper::class.java.simpleName
    }

}