;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; MODULE      : init-scala.scm
;; DESCRIPTION : Initialize scala plugin
;; COPYRIGHT   : (C) 2020  Darcy Shen
;;
;; This software falls under the GNU general public license version 3 or later.
;; It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
;; in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use-modules (dynamic session-edit) (dynamic program-edit))

(define (scala-serialize lan t)
  (with u (pre-serialize lan t)
    (with s (texmacs->code (stree->tree u) "SourceCode")
      (string-append  s  "\n<EOF>\n"))))

(define (sdkman-java-home)
  (with ret (string-append (getenv "HOME")
                           "/.sdkman/candidates/java/current")
    (if (url-exists? ret) ret #f)))

(define (java-home)
  (or (getenv "JAVA_HOME"))
      (sdkman-java-home))

(define (linux-java-cli)
  (with ret "/usr/bin/java"
    (if (url-exists? ret) ret #f)))

(define (home-java-cli)
  (with ret (string-append (java-home) "/bin/java")
    (if (url-exists? ret) ret "#f")))

(define (java-cli)
  (or (home-java-cli)
      (linux-java-cli)))

(define (scala-launcher)
  (string-append
   (java-cli)
   " -jar "
   (getenv "TEXMACS_HOME_PATH")
   "/plugins/scala/texmacs.jar"))

(plugin-configure scala
  (:launch ,(scala-launcher))
  (:tab-completion #f)
  (:serializer ,scala-serialize)
  (:session "Scala")
  (:scripts "Scala"))
