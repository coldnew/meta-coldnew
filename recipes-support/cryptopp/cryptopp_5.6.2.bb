SUMMARY = "C++ class library of cryptographic schemes"
HOMEPAGE = "http://cryptopp.com"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSL-1.0;md5=65a7df9ad57aacf825fd252c4c33288c"

DEPEND = "libtool unzip-native"

SRC_URI = "http://www.cryptopp.com/cryptopp562.zip"
SRC_URI += "file://cryptopp.pc.in"

SRC_URI[md5sum] = "7ed022585698df48e65ce9218f6c6a67"
SRC_URI[sha256sum] = "5cbfd2fcb4a6b3aab35902e2e0f3b59d9171fee12b3fc2b363e1801dfec53574"

S = "${WORKDIR}"

do_compile() {
    # clean all to prevent build error
    oe_runmake -f GNUmakefile clean

    # Build both dynamic and static libs
    oe_runmake -f GNUmakefile dynamic CXXFLAGS="-DNDEBUG -g -O2 -fPIC"
    oe_runmake -f GNUmakefile static

    # build for pkgconfig
    sed s/@VERSION@/${PN}/g ${WORKDIR}/cryptopp.pc.in > ${WORKDIR}/cryptopp.pc
}

do_install() {
    # Rename libcryptopp.so to libcryptopp.so.${PV}
    if [ -e ${S}/libcryptopp.so ]; then
        mv ${S}/libcryptopp.so ${S}/libcryptopp.so.${PV}
    fi

    install -d ${D}${libdir}
    install -m 0644 ${S}/libcryptopp.a ${D}${libdir}
    install -m 0644 ${S}/libcryptopp.so.${PV} ${D}${libdir}
    ln -sf libcryptopp.so.${PV} ${D}${libdir}/libcryptopp.so

    # Since some distro call crypto++ as cryptopp, create symlink for compability
    ln -sf libcryptopp.so.${PV} ${D}${libdir}/libcrypto++.so
    ln -sf libcryptopp.so ${D}${libdir}/libcrypto++.so
    ln -sf libcryptopp.a ${D}${libdir}/libcrypto++.a

    # install headers
    install -d ${D}${includedir}/crypto++
    cp -rf ${S}/*.h ${D}${includedir}/crypto++
    ln -sf crypto++ ${D}${includedir}/cryptopp

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/cryptopp.pc ${D}${libdir}/pkgconfig
}

PACKAGES += " lib${BPN} lib${BPN}-dev lib${BPN}-staticdev lib${BPN}-dbg"
FILES_lib${BPN} = "${libdir}/lib*.so.*"
FILES_lib${BPN}-dev = "${includedir} ${libdir}/lib*.so ${libdir}/lib*.la ${libdir}/pkgconfig"
FILES_lib${BPN}-dbg = "${libdir}/.debug ${prefix}/src/debug"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

BBCLASSEXTEND = "native nativesdk"
