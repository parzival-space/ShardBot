package space.parzival.shardbot.types;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.AuthorInfo;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageEmbed.Footer;
import net.dv8tion.jda.api.entities.MessageEmbed.ImageInfo;
import net.dv8tion.jda.api.entities.MessageEmbed.Provider;
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.api.entities.MessageEmbed.VideoInfo;

@Accessors(chain = true)
@Setter
public class RichEmbedBuilder {
    private String url;
    private String title;
    private String description;
    private OffsetDateTime timestamp;
    private int color = 0xFFFFFF;
    private Thumbnail thumbnail;
    private Provider siteProvider;
    private AuthorInfo author;
    private Footer footer;
    private ImageInfo image;
    private List<Field> fields = new ArrayList<>();


    public RichEmbedBuilder setAuthor(String name, String url, String iconUrl, String proxyIconUrl) {
        return this.setAuthor(new AuthorInfo(name, url, iconUrl, proxyIconUrl));
    }

    public RichEmbedBuilder setFooter(String text, String iconUrl, String proxyIconUrl) {
        return this.setFooter(new Footer(text, iconUrl, proxyIconUrl));
    }

    public RichEmbedBuilder setThumbnail(String url, int width, int height, String proxyUrl) {
        return this.setThumbnail(new Thumbnail(url, proxyUrl, width, height));
    }

    public RichEmbedBuilder setThumbnail(String url, String proxyUrl) {
        return this.setThumbnail(new Thumbnail(url, proxyUrl, 1024, 1024));
    }

    public RichEmbedBuilder setTimestamp(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return this.setTimestamp(OffsetDateTime.of(year, month, dayOfMonth, hour, minute, second, 0,  ZoneOffset.ofTotalSeconds(0)));
    }

    public RichEmbedBuilder setImage(String url, int width, int height, String proxyUrl) {
        return this.setImage(new ImageInfo(url, proxyUrl, width, height));
    }

    public RichEmbedBuilder setImage(String url, String proxyUrl) {
        return this.setImage(new ImageInfo(url, proxyUrl, 1024, 1024));
    }

    public RichEmbedBuilder addField(String title, String description, boolean inline) {
        this.fields.add(new Field(title, description, inline));
        return this;
    }


    public @Nonnull MessageEmbed toMessageEmbed() {
        return new MessageEmbed(
            this.url, this.title, this.description, EmbedType.RICH, 
            this.timestamp, this.color, this.thumbnail, this.siteProvider, 
            this.author, null, this.footer, this.image, this.fields);
    }
}
