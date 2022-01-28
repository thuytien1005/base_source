package wee.digital.sample.data.api

import android.net.http.X509TrustManagerExtensions
import android.util.Base64
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import wee.digital.sample.shared.serviceUrl
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*
import javax.security.cert.CertificateException

object ApiClient {

    private val trustManager: X509TrustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }
    }

    private val sslContext: SSLContext
        get() = SSLContext.getInstance("SSL").also {
            it.init(null, arrayOf(trustManager), SecureRandom())
        }

    private const val publicKey: String = "m9Z7mswlRljf8acQ07EesjKOVJDGy2nR0ZrOM22PE40="

    private var token: String? = null

    private val certPinner: CertificatePinner by lazy {
        CertificatePinner.Builder()
            .add(domainName(serviceUrl), "sha256/$publicKey")
            .build()
    }

    private val debugInterceptor = ApiDebugInterceptor("ApiService")

    val service: ApiService by lazy {
        initService(ApiService::class, serviceUrl) {
            if (serviceUrl.indexOf("https") == -1) {
                if (!publicKey.isNullOrEmpty()) {
                    certificatePinner(certPinner)
                } else {
                    trustClient(this)
                }
            }
            token?.also {
                addInterceptor(authInterceptor(it))
            }
            addInterceptor(debugInterceptor)
        }
    }

    @Throws(SSLException::class)
    private fun validatePinning(
        extensions: X509TrustManagerExtensions,
        connection: HttpsURLConnection,
        validPins: Set<String>
    ) {
        var certChainMsg = ""
        try {
            val md: MessageDigest = MessageDigest.getInstance("SHA-256")
            val trustedChain = trustedChain(extensions, connection)
            for (cert in trustedChain) {
                val publicKey = cert.publicKey.encoded
                md.update(publicKey, 0, publicKey.size)
                val pin: String = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                certChainMsg += "sha256/$pin : ${cert.subjectDN}"
                if (validPins.contains(pin)) {
                    return
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            throw SSLException(e)
        }
        throw SSLPeerUnverifiedException("Peer certificate chain:$certChainMsg")
    }

    @Throws(SSLException::class)
    private fun trustedChain(
        extensions: X509TrustManagerExtensions,
        connection: HttpsURLConnection
    ): List<X509Certificate> {
        val serverCerts: Array<out Certificate> = connection.serverCertificates
        val untrustedCerts: Array<X509Certificate> =
            Arrays.copyOf(serverCerts, serverCerts.size, Array<X509Certificate>::class.java)
        val host: String = connection.url.host
        return try {
            extensions.checkServerTrusted(untrustedCerts, "RSA", host)
        } catch (e: CertificateException) {
            throw SSLException(e)
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    fun String.sha256(): String? {
        val digest = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(digest, Base64.DEFAULT)
    }

    private fun trustClient(client: OkHttpClient.Builder) {
        client.sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
    }

}