import Config from "../config";

class ImageUtils {

    // resource : https://gs.statcounter.com/screen-resolution-stats/mobile/worldwide
    static resolution_width_height_map = Config.resolution_width_height_map

    // resource : https://99designs.com/blog/web-digital/how-to-design-app-icons
    static app_logo_resolution_map = Config.app_logo_resolution_map

    static resolutionCalculate(image_url, callback){
        const img = new Image();
        img.src = image_url;
        var width,height

        img.onload = function() {
            width = img.width
            height = img.height
            var resolution = [{ 'width': width, 'height': height }];
            callback(resolution)
        };
    }

    static toString(resolution_map) {
        var resolution_map_string="{"
        for (const [width, value] of Object.entries(resolution_map)) {
            for (const height of value){
                resolution_map_string+=width + "x" + height + ", "
            }
        }
        resolution_map_string+="}"
        return resolution_map_string
    }
}
export default ImageUtils;