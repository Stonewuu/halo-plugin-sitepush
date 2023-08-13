package run.halo.sitepush.scheme;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "PushLog", group = "sitepush.halo.run",
        version = "v1alpha1", singular = "pushLog", plural = "pushLogs")
@AllArgsConstructor
@NoArgsConstructor
public class PushLog extends AbstractExtension {

    @Schema()
    private Long createTime;

    @Schema(requiredMode = REQUIRED)
    private String pushUrl;

    @Schema(requiredMode = REQUIRED)
    private String pushType;

    @Schema()
    private int pushStatus;
}
