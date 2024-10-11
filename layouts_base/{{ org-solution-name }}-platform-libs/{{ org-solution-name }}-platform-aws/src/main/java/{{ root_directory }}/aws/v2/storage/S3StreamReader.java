// MIT License
//
// Copyright (c) 2017 Wellcome Trust
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package {{ root_package }}.aws.v2.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.util.Enumeration;

/**
 * Read objects from S3, buffering up to `bufferSize` bytes of an object in-memory at a time,
 * minimising the time needed to hold open an HTTP connection to S3.
 *
 * <p>This is useful for reading very large objects to an InputStream, especially where a single
 * GetObject call would time out before reading the entire object.
 */
public class S3StreamReader {
  private static final long DEFAULT_BUFFER_SIZE = 8192;
  private final AmazonS3 s3Client;
  private final long bufferSize;

  public S3StreamReader(AmazonS3 s3Client, long bufferSize) {
    this.s3Client = s3Client;
    this.bufferSize = bufferSize;
  }

  public S3StreamReader(AmazonS3 s3Client) {
    this(s3Client, DEFAULT_BUFFER_SIZE);
  }

  public SequenceInputStream get(String bucketName, String key) {

    var totalSize = getSize(bucketName, key);

    var s3Enumeration = getEnumeration(bucketName, key, totalSize);
    var bufferedEnumeration = getBufferedEnumeration(s3Enumeration);

    return new SequenceInputStream(bufferedEnumeration);
  }

  private long getSize(String bucketName, String key) {
    return s3Client.getObjectMetadata(bucketName, key).getContentLength();
  }

  private Enumeration<S3ObjectInputStream> getEnumeration(
      String bucketName, String key, long totalSize) {
    return new Enumeration<>() {
      private long currentPosition = 0;

      @Override
      public boolean hasMoreElements() {
        return currentPosition < totalSize;
      }

      @Override
      public S3ObjectInputStream nextElement() {
        // The Range request is inclusive of the `start` and `end` parameters,
        // so to read `bufferSize` bytes we need to go to `bufferSize - 1`.
        var request =
            new GetObjectRequest(bucketName, key)
                .withRange(currentPosition, currentPosition + bufferSize - 1);
        currentPosition += bufferSize;

        return s3Client.getObject(request).getObjectContent();
      }
    };
  }

  private Enumeration<ByteArrayInputStream> getBufferedEnumeration(
      Enumeration<? extends InputStream> underlying) {
    return new Enumeration<>() {
      @Override
      public boolean hasMoreElements() {
        return underlying.hasMoreElements();
      }

      @Override
      public ByteArrayInputStream nextElement() {
        try {
          var nextStream = underlying.nextElement();
          var byteArray = IOUtils.toByteArray(nextStream);
          nextStream.close();
          return new ByteArrayInputStream(byteArray);
        } catch (IOException ioException) {
          throw new UncheckedIOException(ioException);
        }
      }
    };
  }
}
