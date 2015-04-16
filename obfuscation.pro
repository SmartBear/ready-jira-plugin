-printmapping proguard-mapping.txt
-allowaccessmodification
-ignorewarnings
-dontshrink

-keep public class * { public *; }
-keep class * { public protected *; }

-dontoptimize

-keep class com.google.inject.Binder
-keep public class com.google.inject.Inject

-keepclassmembers, allowobfuscation class * {
    @com.google.inject.Inject <fields>;
    @com.google.inject.Inject <init>(...);
}

-keepclassmembers, allowobfuscation class * {
    @com.google.inject.Provides *;
}

-keepclassmembers, allowobfuscation public class * {
    @com.google.inject.Inject <fields>;
    @com.google.inject.Inject <init>(...);
}

-keepattributes InnerClasses,SourceFile,LineNumberTable,Deprecated,
                Signature,*Annotation*,EnclosingMethod, Exceptions

-keep class com.smartbear.ready.**Module {
  public protected private *;
}

-keep class com.eviware.soapui.**Module {
  public protected private *;
}

-keep class com.smartbear.servicev.** {
  public protected private *;
}

-keep class com.smartbear.ready.ui.** {
  public protected private *;
}

-keep class com.eviware.loadui.** {
  public protected private *;
}

-keep class com.smartbear.load.** {
  public protected private *;
}

-keepclasseswithmembers class * {
  @com.google.inject.Inject
  <init>(...);
}

-keep class com.eviware.soapui.impl.wsdl.refactoring.RefactoringData$OperationNode {*;}

-keep class com.eviware.soapui.impl.wsdl.refactoring.RefactoringData {
com.eviware.soapui.impl.wsdl.refactoring.RefactoringData$OperationNode getOperationNode(com.eviware.soapui.model.iface.Operation);
}

-keep class com.smartbear.load.facade.** {
  public protected private *;
}

-keep class com.smartbear.ready.security.** {
  public protected private *;
}

#-keepclassmembers class com.smartbear.ready.ui.** { *; }

-keepdirectories
