cmake_minimum_required (VERSION 2.8)
project (repl)
set (CMAKE_BUILD_TYPE Release)

find_package (JNI REQUIRED)
include_directories (${JNI_INCLUDE_DIRS})

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set (SOURCE_FILES repl.cpp)

add_executable(repl ${SOURCE_FILES})
target_link_libraries (repl ${JAVA_JVM_LIBRARY})
