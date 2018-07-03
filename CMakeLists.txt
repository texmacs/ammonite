cmake_minimum_required (VERSION 2.8)
project (tm_scala)
set (CMAKE_BUILD_TYPE Release)

find_package (JNI REQUIRED)
include_directories (${JNI_INCLUDE_DIRS})

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set (SOURCE_FILES tm_scala.cpp)

add_executable(tm_scala ${SOURCE_FILES})
target_link_libraries (tm_scala ${JAVA_JVM_LIBRARY})
