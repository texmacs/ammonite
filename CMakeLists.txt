cmake_minimum_required (VERSION 2.8)
project (scala)
set (CMAKE_BUILD_TYPE Release)

set (PLUGIN $ENV{HOME}/.TeXmacs/plugins/scala)
install (FILES progs/init-scala.scm DESTINATION ${PLUGIN}/progs)
install (FILES target/scala-2.12/texmacs.jar DESTINATION ${PLUGIN})
