#include <iostream>
#include <jni.h>

using namespace std;

int main()
{
    JavaVM *jvm;                      // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                      // Pointer to native interface
    //================== prepare loading of Java VM ============================
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[2];   // JVM invocation options
    options[0].optionString = " "; //-verbose:jni";
    options[1].optionString = "-Djava.class.path=./texmacs.jar";   // where to find java .class
    vm_args.version = JNI_VERSION_1_8;             // minimum Java version
    vm_args.nOptions = 2;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = true;     // invalid options make the JVM init fail
    //=============== load and initialize Java VM and JNI interface =============
    jint rc = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);  // YES !!
    if (rc != JNI_OK) {
           // TO DO: error processing... 
          cin.get();
          exit(EXIT_FAILURE);
    }

    //=============== Display JVM version =======================================
    cout << "JVM load succeeded: Version ";
    jint ver = env->GetVersion();
    cout << ((ver>>16)&0x0f) << "."<<(ver&0x0f) << endl;

    // TO DO: add the code that will use JVM <============  (see next steps)
    jclass cls2 = env->FindClass("TeXmacs");
    if (cls2 == nullptr) {
      cerr << "ERROR: class not found !";
    } else {                                  // if class found, continue
      cout << "Class MyTest found" << endl;
      jmethodID mid = env->GetStaticMethodID(cls2, "hello", "()V");  // find method
      if (mid == nullptr)
          cerr << "ERROR: method void hello() not found !" << endl;
      else {
        env->CallStaticVoidMethod(cls2, mid);                      // call method
        cout << endl;
      }
    }

    jvm->DestroyJavaVM();
    cin.get();
}
