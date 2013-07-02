(ns akvo-tests.api-key
  "ApiKey Smoke Test"
  (:require [clj-http.client :as client])
  (:import java.util.Date
           java.text.SimpleDateFormat
           java.net.URLEncoder
           java.util.TimeZone
           javax.crypto.Mac
           javax.crypto.spec.SecretKeySpec
           org.springframework.security.oauth.common.signature.HMAC_SHA1SignatureMethod))

(def date (Date.))

(defn tsgen 
  "Generates ts parameter for flowservices request"
  []
  (doto (SimpleDateFormat. "yyyy/MM/dd HH:mm:ss")
          (.setTimeZone
            (TimeZone/getTimeZone "GMT"))))

(defn tsenc
  "URL Encodes ts parameter for flowservices request"
  []
  (URLEncoder/encode (.format (tsgen) date) "UTF-8"))

(defn genapikey [secret sign]
  (.sign (HMAC_SHA1SignatureMethod. (SecretKeySpec. (.getBytes "foo") "HMACSHA1")) "bar"))

(def query
      {"action" "listInstance" "includeDate" "true" "surveyId" "394002" "ts" (tsenc) "h" (genapikey "foo" "bar")})
 
(def querynone
      {"action" "listInstance" "includeDate" "true" "surveyId" "394002"})

(defn querystring [params]
  (loop [ks (sort (keys params)) 
         result "" ] 
   (if-not ks
    (subs result 0 (- (count result) 1))
    (recur (next ks) 
          (str result (first ks) "=" (params (first ks)) "&" )))))


(client/get (str "http://flowaglimmerofhope.appspot.com/databackout?" (querystring querynone)))
