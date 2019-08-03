# goNfcLight 
An Android library for enabling third-party application to read and write data into NFC Tag.

### Usage

In the onCreate of your activity or the onCreateView of your fragment, initialize the view with the NfcReadWriteController.

```
private NfcReadWriteController nfcController;
nfcController = new NfcReadWriteController(this);

```

Once the NfcReadWriteController is initialize for checking the NFC functionality is supported by the device we need to check inside onResume of your fragment or activity

```
nfcController.isNfcAvailable(new NfcStatusListener() {
            @Override
            public void nfcProcessOnError(NfcStatusObject object) {
                if (!object.success) {
                    Toast.makeText(getApplicationContext(), "NFC not available !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void nfcMessageOnSuccess(NfcStatusObject object) {

                if (object.success) {
                    Toast.makeText(getApplicationContext(), "NFC available", Toast.LENGTH_SHORT).show();

                }
            }
        });
```

To read the NFC data as NDEF record via intent we need to enableForegroundDispatch in side the onResume.

```
@Override
protected void onResume() {
    super.onResume();
    nfcController.enableForegroundDispatchSystem(this, getClass());
}
```

And also need to disable it so that the activity will listen the dispatch of the intent putting the disableForegroundDispatch in side the onPause

```
@Override
protected void onPause() {
    super.onPause();
    nfcController.disableForegroundDispatchSystem(this);
}
```

To #read the NDEF message from every time we need to to override onNewIntent

```
   @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

            nfcController.readNfcTag(intent, new NfcStatusListener() {
                @Override
                public void nfcProcessOnError(NfcStatusObject object) {
                    Toast.makeText(getApplicationContext(), "status : " + object.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void nfcMessageOnSuccess(NfcStatusObject object) {
                    if (object.isSuccess())
                        editText.setText(object.getMessage());
                    Toast.makeText(getApplicationContext(), "Read tag successful", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
```

For writing to the tag also needed to override the onNewIntent

```
   @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            nfcController.writeNfcTag(intent, editText.getText().toString(), new NfcStatusListener() {
                @Override
                public void nfcProcessOnError(NfcStatusObject object) {
                    Toast.makeText(getApplicationContext(), "status : " + object.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void nfcMessageOnSuccess(NfcStatusObject object) {
                    if (object.isSuccess())
                          Toast.makeText(getApplicationContext(), "Tag written", Toast.LENGTH_SHORT).show();
                }
             });
    }
```

### Download


The latest version can be downloaded in [zip](https://github.com/hathorr-hub/goNfcLight/archive/1.1.zip) and referenced by your application as a library project.

You can also depend on library through Maven :

## Add the JitPack repository to your build file

```
<repositories>
 <repository>
   <id>jitpack.io</id>
   <url>https://jitpack.io</url>
 </repository>
</repositories>
  
```
## Add the dependency

```
<dependency>
  <groupId>com.github.hathorr-hub</groupId>
  <artifactId>goNfcLight</artifactId>
  <version>1.1</version>
</dependency>
```
or gradle:

## Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

```

## Add the dependency

```
dependencies {
	implementation 'com.github.hathorr-hub:goNfcLight:1.1'
}
```


