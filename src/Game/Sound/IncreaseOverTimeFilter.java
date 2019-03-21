package Game.Sound;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This sound filter is used to increase a sounds volume over time.
 */
public class IncreaseOverTimeFilter extends FilterInputStream {

    IncreaseOverTimeFilter(InputStream in) {
        super(in);
    }

    private short getSample(byte[] buffer, int position)  {
        return (short) (((buffer[position+1] & 0xff) << 8) | (buffer[position] & 0xff));
    }

    private void setSample(byte[] buffer, int position, short sample) {
        buffer[position] = (byte)(sample & 0xFF);
        buffer[position+1] = (byte)((sample >> 8) & 0xFF);
    }

    public int read(byte [] sample, int offset, int length) throws IOException {
        int bytesRead = super.read(sample,offset,length);
        float change = 2.5f * (1.0f / (float)bytesRead);
        float volume = 1.0f;
        short amp;

        // Loop through the sample 2 bytes at a time
        for (int p = 0 ; p < bytesRead; p = p + 2) {
            amp = getSample(sample,p);
            amp = (short)((float)amp * volume);
            setSample(sample,p,amp);
            volume += change;
        }

        return length;
    }
}
