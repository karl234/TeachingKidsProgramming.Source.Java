package org.teachingextensions.approvals.lite;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.teachingextensions.approvals.lite.ReporterFactory.FileTypes;
import org.teachingextensions.approvals.lite.namer.ApprovalNamer;
import org.teachingextensions.approvals.lite.namer.JUnitStackTraceNamer;
import org.teachingextensions.approvals.lite.util.ArrayUtils;
import org.teachingextensions.approvals.lite.util.ObjectUtils;
import org.teachingextensions.approvals.lite.util.StringUtils;
import org.teachingextensions.approvals.lite.util.lambda.Function1;
import org.teachingextensions.approvals.lite.writers.ApprovalTextWriter;
import org.teachingextensions.approvals.lite.writers.ComponentApprovalWriter;
import org.teachingextensions.approvals.lite.writers.ImageApprovalWriter;


public class Approvals {
    public static void verify(String response) throws Exception {
        verify(new ApprovalTextWriter(response, "txt"), FileTypes.Text);
    }

    public static <T> void verifyAll(String header, T[] values) {
        Approvals.verifyAll(header, Arrays.asList(values));
    }

    public static <T> void verifyAll(String header, Iterable<T> values) {
        Approvals.verifyAll(header, values, new Function1<T, String>() {
            @Override
            public String call(T i) {
                return i + "";
            }
        });
    }

    public static <T> void verifyAll(String header, T[] values, Function1<T, String> f1) {
        verifyAll(header, Arrays.asList(values), f1);
    }

    public static <T> void verifyAll(String header, Iterable<T> array, Function1<T, String> f1) {
        String text = formatHeader(header) + ArrayUtils.toString(array, f1);
        verify(new ApprovalTextWriter(text, "txt"), FileTypes.Text);
    }

    private static String formatHeader(String header) {
        return StringUtils.isEmpty(header) ? "" : header + "\r\n\r\n\r\n";
    }

    public static void verifyHtml(String response) throws Exception {
        verify(new ApprovalTextWriter(response, "html"), FileTypes.Html);
    }

    public static void verify(Component component) {
        Approvals.verify(new ComponentApprovalWriter(component), FileTypes.Image);
    }

    public static void verify(BufferedImage bufferedImage) {
        verify(new ImageApprovalWriter(bufferedImage), FileTypes.Image);
    }

    public static void verify(ApprovalWriter writer, ApprovalNamer namer, ApprovalFailureReporter reporter) {
        verify(new FileApprover(writer, namer), reporter);
    }

    public static void verify(ApprovalWriter writer, String fileType) {
        verify(writer, createApprovalNamer(), ReporterFactory.get(fileType));
    }

    public static void verify(FileApprover approver, ApprovalFailureReporter reporter) {
        try {
            if (!approver.approve()) {
                boolean passed = false;
                if (reporter instanceof ApprovalFailureOverrider) {
                    passed = approver.askToChangeReceivedToApproved((ApprovalFailureOverrider)reporter);
                }
                if (!passed) {
                    approver.reportFailure(reporter);
                    approver.fail();
                } else {
                    approver.cleanUpAfterSuccess(reporter);
                }
            } else {
                approver.cleanUpAfterSuccess(reporter);
            }
        } catch (Exception e) {
            throw ObjectUtils.throwAsError(e);
        }
    }

    public static ApprovalNamer createApprovalNamer() {
        return new JUnitStackTraceNamer();
    }

    public static void verify(Object o) throws Exception {
        Approvals.verify(o + "");
    }
}
