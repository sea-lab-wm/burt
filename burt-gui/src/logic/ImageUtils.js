import Config from "../config";

class ImageUtils {

    // resource : https://gs.statcounter.com/screen-resolution-stats/mobile/worldwide
    static resolution_width_height_map = Config.resolution_width_height_map

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

    static toString() {
        var resolution_map="{"
        for (const [width, value] of Object.entries(this.resolution_width_height_map)) {
            for (const height of value){
                resolution_map+=width + "x" + height + ", "
            }
        }
        resolution_map+="}"
        return resolution_map
    }
}
export default ImageUtils;