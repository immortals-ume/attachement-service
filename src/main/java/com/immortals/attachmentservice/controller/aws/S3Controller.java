package com.immortals.attachmentservice.controller.aws;

import com.immortals.attachmentservice.model.payload.aws.BucketPayload;
import com.immortals.attachmentservice.model.payload.aws.FilePayload;
import com.immortals.attachmentservice.model.payload.aws.S3ObjectResponsePayload;
import com.immortals.attachmentservice.service.attachment.AttachmentService;
import com.immortals.attachmentservice.service.aws.s3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Controller Class Implements various rest controller methods create , update , get , getById, delete attachments in
 * s3 storage
 */
@RestController
@RequestMapping(value = "/api/v1/s3/")
public class S3Controller {


    private final s3Service attachmentService;


    @Autowired
    public S3Controller(AttachmentService service) {
        attachmentService = service;
    }

    /**
     * Lets Admin create a s3 bucket in AWS .
     *
     * @param bucketName name of bucket to be created
     * @return status of bucket creation .
     * <p>
     *     <ul>
     *       if already exists , then bucket already exists with provided name
     *       if no bucket is found , creates the bucket
     *     </ul>
     * </p>
     */
    @PostMapping(value = "create/{bucketName}")
    @ResponseStatus(HttpStatus.CREATED)
    public String createdBucket(@Valid @PathVariable String bucketName) {
        return attachmentService.createBucket(bucketName);
    }


    /**
     * List of Bucket is returned present in s3 storage  for the client which is configured by providing aws
     * credentials in the application.properties
     *
     * @return list of buckets for the specified account
     */
    @GetMapping(value = "/buckets")
    @ResponseStatus(HttpStatus.OK)
    public List<BucketPayload> getListOfBuckets() {
        return attachmentService.listOfBucketsInS3();
    }

    /**
     * Upload the file using the System path provided in the File Payload
     *
     * @param filePayload provides the attributes for the file Upload
     *                    <p>
     *                    <o>
     *                    fileName : name of the file -----------------------------------required
     *                    bucketName : name of S3 Bucket to which the file to uploaded---required
     *                    file Path : path of the file , where the file is to be picked- required
     *                    key : Object Key(S3 storage) user Provided ------------------- required
     *                    file Metadata :  user provided file MetaData ----------------- Optional
     *                    </o>
     *                    </p>
     * @return eTag of the File Uploaded .
     */
    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadFileFromFileSystem(@RequestBody FilePayload filePayload) {
        return attachmentService.upload(filePayload);
    }

    /**
     * Upload the file as a multipart upload
     *
     * @param multipartFile file as a multipart form data
     * @param filePayload   provides the attributes for the file Upload
     *                      <p>
     *                      <o>
     *                      fileName : name of the file -----------------------------------required
     *                      bucketName : name of S3 Bucket to which the file to uploaded---required
     *                      file Path : path of the file , where the file is to be picked- optional
     *                      key : Object Key(S3 storage) user Provided    ---------------  required
     *                      file Metadata :  user provided file MetaData                   Optional
     *                      </o>
     *                      </p>
     * @return etag of the successful uploaded file
     */

    @PostMapping(value = "/upload/multipart", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadAsMultipartFile(@RequestPart MultipartFile multipartFile,
                                        @RequestPart FilePayload filePayload) {
        return attachmentService.uploadAsMultipartFile(multipartFile, filePayload);
    }

    /**
     * get the list of Objects in the bucket S3
     *
     * @param bucketName name of the bucket for which the list of objects to be found
     * @return list of objects in the bucket
     */
    @GetMapping(value = "/{bucketName}")
    @ResponseStatus(HttpStatus.OK)
    public List<S3ObjectResponsePayload> getListOfFilesInBucket(@PathVariable String bucketName) {
        return attachmentService.getListOfFiles(bucketName);
    }
}
