package org.nicodem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    private static final File UCON_64_WORKING_DIRECTORY = new File("C:\\Users\\Stefan Nicodem\\IdeaProjects\\SNES Rom Analyzer\\ucon64");
    private static final List<Ucon64DTO> UCON_64_DTOS = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting SNES ROM Analyzer main");
        long start = System.currentTimeMillis();

        File rootDirectory = new File("D:/GoodSNES - v2.04/");
        traverseDirectory(rootDirectory);

        try {
            FileWriter fileWriter = new FileWriter(new File("snesgames.csv"));
            for (Ucon64DTO ucon64DTO : UCON_64_DTOS) {
                fileWriter.write(ucon64DTO.toCsvString() + System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SNES ROM Analyzer done, ran for " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    }

    private static void traverseDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseDirectory(file);
                    } else if (isElegibleForUcon64(file)) {
                        try {
                            runUcon64(file);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            System.err.println("No directory selected");
        }
    }

    private static boolean isElegibleForUcon64(File file) {
        return !file.getName().startsWith("BS") && (file.getName().endsWith("(U) [!].smc") || file.getName().endsWith("(E) [!].smc") || file.getName().endsWith("(J) [!].smc")
                || file.getName().endsWith("(U).smc") || file.getName().endsWith("(E).smc") || file.getName().endsWith("(J).smc") || file.getName().endsWith("(V1.0).smc")
                || file.getName().endsWith("(V1.1).smc") || file.getName().endsWith("(V1.2).smc"));
    }

    private static void runUcon64(File file) throws IOException, InterruptedException {
        String command = "ucon64/ucon64.exe \"" + file.getAbsolutePath() + "\"";
        Process process = Runtime.getRuntime().exec(command, null, UCON_64_WORKING_DIRECTORY);
        process.waitFor();

        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        int read = inputStreamReader.read();
        while (read != -1) {
            stringBuilder.append((char) read);
            read = inputStreamReader.read();
        }

        parseUcon64Output(stringBuilder.toString());
    }

    private static void parseUcon64Output(String ucon64Output) {
        if (ucon64Output != null && !Objects.equals(ucon64Output, "")) {
            String[] lines = ucon64Output.split(System.lineSeparator());
            int length = lines.length;
            if (length >= 30) {
                Ucon64DTO ucon64DTO = new Ucon64DTO(ucon64Output);
                if (checkUcon64DTO(ucon64DTO)) {
                    UCON_64_DTOS.add(ucon64DTO);
                }
            }
        }
    }

    private static boolean checkUcon64DTO(Ucon64DTO ucon64DTO) {
        return ucon64DTO.getSizeInMbit() >= 4 && !ucon64DTO.getGameName().startsWith("DAT");
    }
}
