package org.nicodem;

public class Ucon64DTO {

    private String path;
    private String region;
    private Integer sizeInBytes;
    private Integer sizeInMbit;
    private Boolean hiRom;
    private Boolean sram;
    private Integer sramSizeInKiloBytes;
    private String specialChip;
    private String gameName;
    private Boolean battery;

    public Ucon64DTO(String s) {
        String[] lines = s.split(System.lineSeparator());
        this.path = lines[4].split("\\\\")[2];
        String region = lines[15];
        if (region.equals("U.S.A.") || region.equals("Japan")) {
            this.region = "NTCS";
        } else if (region.equals("Europe, Oceania and Asia") || region.equals("Germany, Austria and Switzerland")) {
            this.region = "PAL";
        } else {
            this.region = region;
        }
        // ROM size parse
        String[] sizeParts = lines[16].split(" Bytes ");
        sizeInBytes = Integer.parseInt(sizeParts[0]);
        sizeInMbit = Integer.parseInt(sizeParts[1].substring(1, sizeParts[1].indexOf('.')));
        this.hiRom = lines[21].equals("HiROM: Yes");
        // ROM Type parse
        String[] typeParts = lines[23].split(" \\+ ");
        if (typeParts.length > 0) {
            for (String typePart : typeParts) {
                if (!typePart.startsWith("ROM type") && !typePart.equals("SRAM") && !typePart.equals("Battery")) {
                    if (this.specialChip == null) {
                        this.specialChip = typePart;
                    } else {
                        this.specialChip += "," + typePart;
                    }
                } else if (typePart.equals("Battery")) {
                    battery = true;
                }
            }
        }
        // Battery
        if (battery == null) {
            battery = false;
        }
        // SRAM parse
        String[] sramParts = lines[25].split(", ");
        this.sram = sramParts[0].equals("SRAM: Yes");
        if (this.sram) {
            this.sramSizeInKiloBytes = Integer.parseInt(sramParts[1].substring(0, sramParts[1].indexOf(" ")));
        }
        if (lines.length >= 33) {
            this.gameName = lines[32].trim();
        } else {
            this.gameName = path;
        }
    }

    public String getPath() {
        return path;
    }

    public String getRegion() {
        return region;
    }

    public Integer getSizeInBytes() {
        return sizeInBytes;
    }

    public Integer getSizeInMbit() {
        return sizeInMbit;
    }

    public Boolean getHiRom() {
        return hiRom;
    }

    public Boolean getSram() {
        return sram;
    }

    public Integer getSramSizeInKiloBytes() {
        return sramSizeInKiloBytes;
    }

    public String getSpecialChip() {
        return specialChip;
    }

    public String getGameName() {
        return gameName;
    }

    public Boolean getBattery() {
        return battery;
    }

    @Override
    public String toString() {
        return "Ucon64DTO{" +
                "path='" + path + '\'' +
                ", region='" + region + '\'' +
                ", sizeInBytes=" + sizeInBytes +
                ", sizeInMbit=" + sizeInMbit +
                ", hiRom=" + hiRom +
                ", sram=" + sram +
                ", sramSizeInKiloBytes=" + sramSizeInKiloBytes +
                ", specialChip='" + specialChip + '\'' +
                ", gameName='" + gameName + '\'' +
                ", battery=" + battery +
                '}';
    }

    public String toCsvString() {
        return gameName + ";" + region + ";" + sizeInMbit + ";" + (hiRom ? "HiROM" : "LoROM") + ";" + (sram ? "Yes" : "No") + ";" + (sramSizeInKiloBytes == null ? "" : sramSizeInKiloBytes * 8)
                + ";" + (specialChip == null ? "" : specialChip) + ";" + (battery ? "Yes" : "No");
    }
}
