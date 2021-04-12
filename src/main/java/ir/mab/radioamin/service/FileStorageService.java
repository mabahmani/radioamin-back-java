package ir.mab.radioamin.service;

import ir.mab.radioamin.exception.FileStorageException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.StorageType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    private final String rootPath;
    private final String basePath;
    private final String uploadDirPath;
    private final String uploadDirUrl;

    public FileStorageService() {
        Date date = new Date();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMMM_dd");
        yearFormat.format(date);
        monthDayFormat.format(date);
        rootPath = File.separator + "Users" + File.separator + "mab" + File.separator + "IdeaProjects" + File.separator + "radioamin";
        uploadDirPath = File.separator + "uploads" + File.separator + yearFormat.format(date) + File.separator + monthDayFormat.format(date);
        uploadDirUrl = "/" + "uploads" + "/"+ yearFormat.format(date) + "/" + monthDayFormat.format(date);
        basePath = rootPath + uploadDirPath;
    }

    public String storeFile(StorageType storageType, String attrName, MultipartFile file) {

        final Path fileStorageLocation;
        final String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        final String downloadDirUrl;

        switch (storageType) {
            case USER_AVATAR:
                downloadDirUrl = uploadDirUrl + "/" + "avatars" + "/" + "users" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "avatars" + File.separator + "users" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case SINGER_AVATAR:
                downloadDirUrl = uploadDirUrl + "/" + "avatars" + "/" + "singers" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "avatars" + File.separator + "singers" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case MUSIC_COVER:
                downloadDirUrl = uploadDirUrl + "/" + "covers" + "/" + "musics" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "covers" + File.separator + "musics" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case ALBUM_COVER:
                downloadDirUrl = uploadDirUrl + "/" + "covers" + "/" + "albums" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "covers" + File.separator + "albums" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case PLAYLIST_COVER:
                downloadDirUrl = uploadDirUrl + "/" + "covers" + "/" + "playlists" + "/"+ attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "covers" + File.separator + "playlists" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case MUSIC:
                downloadDirUrl = uploadDirUrl + "/" + "musics" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "musics" + File.separator + attrName).toAbsolutePath().normalize();
                break;
            case VIDEO:
                downloadDirUrl = uploadDirUrl + "/" + "videos" + "/"+ attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "videos" + File.separator + attrName).toAbsolutePath().normalize();
                break;

            default:
                downloadDirUrl = uploadDirUrl + "/" + "defaults" + "/" + attrName;
                fileStorageLocation = Paths.get(basePath + File.separator + "defaults" + File.separator + attrName).toAbsolutePath().normalize();
        }
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
        }

        try {
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
        return downloadDirUrl + "/" + fileName;
    }

    public Resource loadFileAsResource(String fileUrl) {
        try {
            Path filePath = Paths.get(rootPath + fileUrl);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File",fileUrl,"url");
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File",fileUrl,"url");
        }
    }
}
