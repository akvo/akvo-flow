(ns akvo-tests.api-key
  "ApiKey Smoke Test"
  (:require [clj-http.client :as client])
  (:import java.util.Date
           java.text.SimpleDateFormat
           java.net.URLEncoder
           java.util.TimeZone
           javax.crypto.spec.SecretKeySpec
           org.springframework.security.oauth.common.signature.HMAC_SHA1SignatureMethod))

(defn- dformat []
  (doto (SimpleDateFormat. "yyyy/MM/dd HH:mm:ss")
          (.setTimeZone
            (TimeZone/getTimeZone "GMT"))))

(defn generate-timestamp
  "Returns the current date using the format yyyy/MM/dd HH:mm:ss"
  []
  (.format (dformat) (Date.)))

(defn generate-apikey
  "Generates a HMAC-SHA1 signature based on a secret"
  [secret sign]
  (.sign (HMAC_SHA1SignatureMethod. (SecretKeySpec. (.getBytes secret) "HMAC-SHA1")) sign))

(defn generate-query-string
  "Returns a string suitable for a GET request based on a Map of parameters
   The parameters are sorted by key, Note: The values are url encoded e.g.

   {\"includeDate\" \"true\"}
    \"action\" \"listInstance\"
    \"ts\" \"2013/07/02 08:09:55\"}

   Returns: action=listInstance&includeDate=true&ts=2013%2F07%2F02+08%3A09%3A55"
  [params]
  (loop [ks (sort (keys params))
         result ""]
   (if-not ks
    (subs result 0 (- (count result) 1)) ;; removing the last &
    (recur (next ks)
           (str result (first ks) "=" (URLEncoder/encode (params (first ks)) "UTF-8") "&" )))))