package org.recognition.fingerprint;

import org.recognition.utils.Consts;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Links extends ArrayList<Links.Link> {

    public Links(List<Peak> peakList) {
        for (int i = 0; i < peakList.size() - 1; i++) {
            Peak p = peakList.get(i);

            List<Peak> tmpPeaks = peakList
                    //Extract a sublist from i+1 to the end
                    .subList(i + 1, peakList.size() - 1)
                    .stream().filter(a -> {
                        boolean b;
                        //Time difference not less than 1
                        b = Consts.times[a.getTime()] - Consts.times[p.getTime()] >= 1f;
                        //Time difference not more than 3
                        b = b && Consts.times[a.getTime()] - Consts.times[p.getTime()] < 3f;
                        //In the same band too
                        b = b && a.compareBand(p);

                        return b;
                    }).collect(Collectors.toList());

            for (Peak a : tmpPeaks)
                super.add(new Link(p,a));
        }
    }

    public class Link {
        private String hash;
        private int time;

        Link(Peak start, Peak end) {
            int deltaTime = end.getTime() - start.getTime();
            int deltaFreq = end.getFreq() - start.getFreq();

            hash = DigestUtils.sha1Hex(deltaTime + "" + deltaFreq + "" + start.getFreq());
            time = start.getTime();
        }

        public String getHash() {
            return hash;
        }

        public int getTime() {
            return time;
        }
    }
}
