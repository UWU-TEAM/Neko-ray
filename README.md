# AndroidLibXrayLite

## Build requirements
* JDK
* Android SDK , NDK
* Go
* gomobile

## Build instructions
1. `install java 17+ , android sdk 34 and ndk 26.2, go 1.22`
2. `go install golang.org/x/mobile/cmd/gomobile@latest`
3. `go install golang.org/x/mobile/cmd/gobind@latest`
4. `git clone [repo] && cd AndroidLibXrayLite`
5. `go get`
6. `gomobile init`
7. `go mod tidy -v`
8. `gomobile bind -v -androidapi 21 -ldflags='-s -w' ./`

This version is different from the original version and has some customization for use in https://github.com/dev7dev/V2ray-Android.