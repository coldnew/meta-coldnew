SUMMARY = "A caching web proxy"
HOMEPAGE = "http://www.pps.univ-paris-diderot.fr/~jch/software/polipo/"
SECTION = "network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "http://www.pps.univ-paris-diderot.fr/~jch/software/files/${PN}/${PN}-${PV}.tar.gz \
        file://config \
        file://polipo.service \
        "

SRC_URI[md5sum] = "defdce7f8002ca68705b6c2c36c4d096"
SRC_URI[sha256sum] = "f6458a3ab2548280d4f5596f8d5ae60c61ddf7147ee0b3bb2d67b96da49c0436"

S = "${WORKDIR}/${PN}-${PV}"

inherit systemd

SYSTEMD_SERVICE_${PN} = "polipo.service"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${sysconfdir}/polipo
    install -m 0666 ${WORKDIR}/config ${D}${sysconfdir}/polipo

    install -d ${D}${bindir}
    install -m 0755 ${S}/polipo ${D}${bindir}

    if ${@base_contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/polipo.service ${D}${systemd_unitdir}/system
    fi
}
