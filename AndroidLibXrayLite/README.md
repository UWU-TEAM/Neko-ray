# AndroidLibXrayLite

## Build requirements
* JDK
* Android SDK , NDK
* Go
* gomobile

## Build instructions
1. `install java 17+ , android sdk 34 and ndk 26.2, go 1.22` # don`t forget to set path!
2. `go install golang.org/x/mobile/cmd/gomobile@latest`
3. `go install golang.org/x/mobile/cmd/gobind@latest`
4. `git clone [repo] && cd AndroidLibXrayLite`
5. `go get`
6. `gomobile init`
7. `go mod tidy -v`
8. `gomobile bind -v -androidapi 21 -ldflags='-s -w' ./`

## Dev7 customizations
This version is different from the original version and has some customization for use in [V2ray-Android](https://github.com/dev7dev/V2ray-Android).
It has been tried to convert uri to json inside the library by using [gvcgo/vpnparser](https://github.com/gvcgo/vpnparser) module, plus Some changes for personalization 
