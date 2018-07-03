cmake_minimum_required (VERSION 2.8)
project (tm_scala)
set (CMAKE_BUILD_TYPE Release)
SET (CMAKE_INSTALL_RPATH_USE_LINK_PATH true)

find_package (JNI REQUIRED)
include_directories (${JNI_INCLUDE_DIRS})

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set (SOURCE_FILES tm_scala.cpp)

add_executable (tm_scala ${SOURCE_FILES})
get_filename_component (JAVA_JVM_LIBRARY_DIR ${JAVA_JVM_LIBRARY} DIRECTORY)
message (${JAVA_JVM_LIBRARY_DIR})
target_link_libraries (tm_scala ${JAVA_JVM_LIBRARY} )

set (PLUGIN $ENV{HOME}/.TeXmacs/plugins/scala)

install (FILES progs/init-scala.scm DESTINATION ${PLUGIN}/progs)
install (FILES target/scala-2.12/texmacs.jar DESTINATION ${PLUGIN})
install (TARGETS tm_scala DESTINATION ${PLUGIN}/bin)