f_mount_sdcard() {
    if [ -d "$MNT/sdcard" ] && $BUSYBOX mountpoint -q "$MNT/sdcard"; then
        return 0
    fi

    mkdir -p "$MNT/sdcard"
    local sdcard_paths=(
        "$EXTERNAL_STORAGE"
        /storage/emulated/0
        /storage/emulated/legacy
        /storage/sdcard0
        /sdcard
    )

    for sdcard in "${sdcard_paths[@]}"; do
        if [ -d "$sdcard" ] && $BUSYBOX mount -o bind "$sdcard" "$MNT/sdcard"; then
            echo "[+] Mounted /sdcard"
            return 0
        fi
    done

    echo "[-] Failed to mount /sdcard"
    return 6
}