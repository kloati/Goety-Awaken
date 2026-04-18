package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.enums.ModChatFormatting;
import com.k1sak1.goetyawaken.client.typography.GATextMetadata;
import com.k1sak1.goetyawaken.client.typography.GATextPipeline;
import net.minecraft.network.chat.TextColor;

public class ModernUIErosionRenderer {
   public static void registerCalls() {
      TextColor erosionColor = TextColor.fromLegacyFormat(ModChatFormatting.EROSION);
      GATextPipeline.GATextEffectData metadata = GATextPipeline.GATextEffectData.of("goetyawaken:erosion");
      GATextMetadata.register(erosionColor, metadata);
      GoetyAwaken.LOGGER.info("Registered Erosion effect for ModernUI compatibility");
   }
}
