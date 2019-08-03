# goNfcLight 
An Android library for enabling third-party application to read and write data into NFC Tag.

### Usage

In the onCreate of your activity or the onCreateView of your fragment, initialize the view with the NfcReadWriteController.

```
private NfcReadWriteController nfcController;
nfcController = new NfcReadWriteController(this);

```


### Download


The latest version can be downloaded in zip and referenced by your application as a library project.

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


