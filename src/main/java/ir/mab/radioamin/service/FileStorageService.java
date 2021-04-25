package ir.mab.radioamin.service;

import ir.mab.radioamin.exception.FileStorageException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.StorageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String rootPath;
    private String downloadDirUrl;

    public String storeFile(StorageType storageType, String identifierName, MultipartFile file) {

        final Path fileStorageLocation = generateFileStorageLocation(storageType, identifierName);
        final String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
        }

        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return downloadDirUrl + "/" + fileName;
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
    }

    public Resource loadFileAsResource(String fileUrl) {
        try {
            Path filePath = Paths.get(rootPath + fileUrl);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", fileUrl, "url");
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", fileUrl, "url");
        }
    }

    public void deleteFile (String fileUrl) {
        try {
            Path filePath = Paths.get(rootPath + fileUrl);
            Files.delete(filePath);
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", fileUrl, "url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path generateFileStorageLocation(StorageType storageType, String identifierName) {
        Date date = new Date();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMMMdd");
        yearFormat.format(date);
        monthDayFormat.format(date);

        String uploadDirPath = "/" + "uploads" + "/" + yearFormat.format(date) + "/" + monthDayFormat.format(date);

        switch (storageType) {
            case USER_AVATAR:
                downloadDirUrl = uploadDirPath + "/" + "avatars" + "/" + "users" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case SINGER_AVATAR:
                downloadDirUrl = uploadDirPath + "/" + "avatars" + "/" + "singers" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case MUSIC_COVER:
                downloadDirUrl = uploadDirPath + "/" + "covers" + "/" + "musics" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case ALBUM_COVER:
                downloadDirUrl = uploadDirPath + "/" + "covers" + "/" + "albums" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case PLAYLIST_COVER:
                downloadDirUrl = uploadDirPath + "/" + "covers" + "/" + "playlists" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case MUSIC:
                downloadDirUrl = uploadDirPath + "/" + "musics" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            case VIDEO:
                downloadDirUrl = uploadDirPath + "/" + "videos" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
            default:
                downloadDirUrl = uploadDirPath + "/" + "defaults" + "/" + identifierName;
                return Paths.get(rootPath + downloadDirUrl).toAbsolutePath().normalize();
        }
    }
}
