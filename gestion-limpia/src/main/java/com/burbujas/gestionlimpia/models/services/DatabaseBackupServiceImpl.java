package com.burbujas.gestionlimpia.models.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Service("DatabaseBackupServiceImpl")
public class DatabaseBackupServiceImpl implements IDatabaseBackupService {

    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;
    @Value("${app.database.name}")
    private String databaseName;
    @Value("${app.database.tmp_path}")
    private String tmpPath; // termina en /

    @Override
    public Map<Integer, String> crearBackup() throws IOException, InterruptedException {
        // Ruta del archivo de respaldo generado
        Instant ahora = Instant.now();
        String backupFilePath = this.tmpPath + "db_burbujas_backup_" + ahora.toString() + ".sql";
        File tempFile = new File(backupFilePath);

        String[] comando = new String[]{"sh", "-c", "mysqldump --user " + databaseUsername + " --password=" + databasePassword + " --opt " + databaseName + " > " + backupFilePath};
        Process process = Runtime.getRuntime().exec(comando);
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Copia de seguridad creada exitosamente en: " + backupFilePath);
        } else {
            System.out.println("Error creando la copia de seguridad. Código de salida: " + exitCode);
            throw new RuntimeException("Error creando la copia de seguridad. Código de salida: " + exitCode);
        }

        // retorna el código de salida del proceso y el path del archivo creado
        return Map.of(exitCode, backupFilePath);
    }

    public void deleteTempFile(String backupFilePath) {
        // eliminar el archivo temporal
        System.out.println(backupFilePath);
        new File(this.tmpPath + backupFilePath).delete();
    }

    @Override
    public int restaurarBackup(MultipartFile archivoBackup) throws IOException, InterruptedException {
        // Guardar el archivo temporalmente para la restauración
        String backupFilePath = this.tmpPath + archivoBackup.getOriginalFilename();

        File tempFile = new File(backupFilePath);

        archivoBackup.transferTo(tempFile);

        String[] comando = new String[]{"sh", "-c", "mysql --user " + databaseUsername + " --password=" + databasePassword + " " + databaseName + " < " + backupFilePath};
        Process process = Runtime.getRuntime().exec(comando);

        return process.waitFor();
    }

}
