#include <iostream>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <jni.h>

using namespace std;

#define DATA_BEGIN   ((char) 2)
#define DATA_END     ((char) 5)
#define DATA_ESCAPE  ((char) 27)

JavaVM *jvm;
static int buffer_len= 4096;
static char buffer [4096];

void read_input () {
  int i;
  for (i=0; i<buffer_len-1; i++) {
    buffer[i]= fgetc (stdin);
    if (buffer[i] == '\n') break;
  }
  buffer[i]='\0';
}

JNIEnv* initJava() {
  JNIEnv *env;
  JavaVMInitArgs vm_args;
  JavaVMOption* options = new JavaVMOption[2];
  char class_path[256]="-Djava.class.path=";
  strcat(class_path, getenv("HOME"));
  strcat(class_path, "/.TeXmacs/plugins/scala/texmacs.jar");
  options[0].optionString = " "; //-verbose:jni";
  options[1].optionString = class_path;
  vm_args.version = JNI_VERSION_1_8;
  vm_args.nOptions = 2;
  vm_args.options = options;
  vm_args.ignoreUnrecognized = true;
  jint rc = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);  // YES !!
  if (rc != JNI_OK) {
    exit(EXIT_FAILURE);
  }
  return env;
}

int main()
{
  JNIEnv* env = initJava();
  cout << DATA_BEGIN << "verbatim:";
  cout << "JVM Version:";
  jint ver = env->GetVersion();
  cout << ((ver>>16)&0x0f) << "."<<(ver&0x0f) << endl;
  cout << DATA_END;
  cout.flush ();

  jclass tm = env->FindClass("org/texmacs/TeXmacs");
  if (tm == nullptr) {
    cerr << "ERROR: org.texmacs.TeXmacs not found";
    exit(-1);
  }
  jmethodID eval = env->GetStaticMethodID(tm, "eval", "(Ljava/lang/String;)Ljava/lang/String;");
  if (eval == nullptr) {
    cerr << "ERROR: eval method not found";
    exit(-1);
  }

  int count = 1;
  while (true) {
    read_input ();
    if (strcmp(buffer, "<EOF>") == 0) {
      continue;
    }
    if (strcmp(buffer, "exit") == 0) {
      cout << DATA_BEGIN << "verbatim:";
      cout << "Bye!" << endl;
      cout << DATA_END;
      cout.flush ();
      break;
    }
    string v = string(buffer);
    bool is_blank = all_of(v.cbegin(), v.cend(), [](char i){ return isblank(i); });
    if (is_blank)
      continue;
    jstring input = env->NewStringUTF(buffer);
    jstring output = (jstring) env->CallStaticObjectMethod(tm, eval, input);
    const char *nativeString = env->GetStringUTFChars(output, JNI_FALSE);
    cout << DATA_BEGIN << "verbatim:";
    cout << nativeString;
    cout << DATA_END;
    cout.flush ();
    env->ReleaseStringUTFChars(output, nativeString);
    count ++;
  }

  jvm->DestroyJavaVM();
}
