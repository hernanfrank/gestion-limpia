package com.burbujas.gestionlimpia.models.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IDatabaseBackupService {

    public Map<Integer,String> crearBackup() throws IOException, InterruptedException;

    public int restaurarBackup(MultipartFile nombreArchivo) throws IOException,InterruptedException;

    public void deleteTempFile(String backupFilePath);

}
