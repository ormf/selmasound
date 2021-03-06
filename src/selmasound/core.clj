(ns selmasound.core
  (:require [selmasound.api]))

(selmasound.api/immigrate-selmasound-api)

(comment
  (selmasound)
  (use 'selmasound.core)
  (use 'overtone.live)
  ((selmasound "prepp_57.wav"))

  (def snare (freesound 26903))
  (def kick (freesound 777))
  (def kick2 (freesound 2086))
  (def cl-hat (freesound 802))
  (cl-hat)
  (kick2)
  (def lm-ges (selmasound "lm-ges-16-mn.wav"))
  (testsamp :start 0 :end 0.01)
  (:duration testsamp)
  (def
    lachenmann-marks
    (map #(apply assoc {} %)
         '((:text "Komponieren" :start 3222956 :prop 292996/41300461 :dur 44100) (:text "heisst" :start 3375856 :prop 306896/41300461 :dur 22000) (:text "Ueber Musik Nachdenken" :start 3455874 :prop 3455874/454305071 :dur 44100) (:text "Musik1" :start 3461996 :prop 3461996/454305071 :dur 15000) (:text "Musik2" :start 3542474 :prop 3542474/454305071 :dur 15100) (:text "Musik3" :start 3610026 :prop 3610026/454305071 :dur 15000) (:text "Horizont" :start 3842689 :prop 3842689/454305071 :dur 24000) (:text "Wertvorstellung" :start 3876985 :prop 3876985/454305071 :dur 34500) (:text "Begriff1" :start 3937327 :prop 3937327/454305071 :dur 20100) (:text "Musik4" :start 4124497 :prop 4124497/454305071 :dur 15100) (:text "Begriff2" :start 4318828 :prop 4318828/454305071 :dur 20100) (:text "Form" :start 4363810 :prop 396710/41300461 :dur 24100) (:text "Weise" :start 4625424 :prop 4625424/454305071 :dur 17100) (:text "Frage" :start 4647615 :prop 4647615/454305071 :dur 16900) (:text "Musikbegriff" :start 4771413 :prop 4771413/454305071 :dur 21500) (:text "Zivilisation" :start 4965501 :prop 4965501/454305071 :dur 32100) (:text "Service" :start 8122018 :prop 8122018/454305071 :dur 44100) (:text "Idylle" :start 8239681 :prop 8239681/454305071 :dur 27100) (:text "Emphase" :start 8290920 :prop 753720/41300461 :dur 37600))))

  (map #(at (+ (now) (* % 3000)) (play-lm %)) (range (count lachenmann-marks)))

  (:dur (first lachenmann-marks))
  (#(at (+ now (* % 1000)) (play-lm %)) 0)


  (defn play-lm [idx]
    (let [len (* 44100 (int (:duration lm-ges)))
          mark (nth lachenmann-marks idx)
          start (:start mark)
          end (+ start (:dur mark))]
      (lm-ges :start (/ start len)
              :end (/ end len))))

  (defn play-lm [idx]
    (let [len (* 44100 (int (:duration lm-ges)))
             mark (nth lachenmann-marks idx)
             start (:start mark)
             end (+ start (:dur mark))]
     (list :start (/ start len)
           :end (/ end len)))

    (lm-ges :start 0.2 :end 0.2005 :amp 1))
  (stop)
  (* 44100 (int (:duration lm-ges)))

  (:start  (nth lachenmann-marks 0))

(play-lm 2)
  
  (lm-ges)
  (stop)
  
  (let)
  (apply-at (+ (now) 1000) #(testsamp :start 0.2 :end 0.23))
  (testsamp :start 0.4 :end 0.45)
  (let [sample testsamp
        dur (:duration testsamp)
        start 8.4]
    (map
     #(at (+ (now) (* % 20)) (sample :start (/ (+ start (* 0.001 %)) dur) :end (/ (+ start 0.1 (* 0.001 %)) dur)))
     (range 1)))

  (map float '(25299/463616 1909/28976 78211/927232 82839/927232 10567/115904 48747/463616 108917/927232 282609/1854464 167897/927232 44601/231808 366681/1854464 372851/1854464 380873/1854464))



  (let [sample testsamp
        samplen (:duration testsamp)
        dur 0.05
        marks '(0.054568868 0.06588211 0.0843489 0.0893401 0.09117028 0.10514521 0.11746467 0.15239389 0.18107334 0.19240493 0.19772883 0.20105594 0.20538172)]
    (map
     #(let [pos (rand-elt marks)]
        (at (+ (now) (* %1 dur 1000)) (sample :amp 16 :start pos :end (+ pos (/ dur samplen 0.5)))))
     (range 800)))

  (defn rand-elt [list]
    (nth list (rand (count list))))

  (for [x (range 100)]
    (rand-elt '(1 2 3 4)))

  (for [x (range 100)]
    (rand-elt '(1 2 3 4)))
  
  (/ 1000 (:duration testsamp) 2000)

  (map #(* % (:duration testsamp)) '(0.054568868 0.06588211 0.0843489 0.11746467 0.18107334))

  (testsamp :start 0.054568868 :end 0.06588211)
   
  
  
  (let [sample testsamp
        dur (:duration testsamp)
        start 8.4]
    (map
     #(at (+ (now) (* % (+ (rand 10) 20))) (sample :start (/ (+ start (* 0.001 %)) dur) :end (/ (+ start 0.1 (* 0.001 %)) dur)))
     (range 1800)))
  (vumeter)
  (stop)
  (newsnare)
  
  (selmasound-url "lm01-16.wav")
  (selmasound-sample "lm01-16.wav")
  (selmasound-path "lm01-16.wav")
  (selmasound-info "lm01-16.wav")
  (asset/asset-path (info-url "lm02-16.wav"))
  (fetch-cached-path url name)
  (info-url "lm02.wav")
  (freesound-info 26903)


  (apply selmasound-sample "lm03-16.wav" '())
  (apply overtone.sc.sample/load-sample (selmasound-path "lm01-16.wav"))
  
  (last (clojure.string/split (info-url "lm02-16.wav") #"/"))
  )
