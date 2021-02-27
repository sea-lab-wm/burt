import React , { Component }  from "react";
import ImagePicker from 'react-image-picker'
import "./SelectOneScreenOption.css";
import img1 from "../../assets/images/s1.png"
import img2 from "../../assets/images/s2.png"
import img3 from "../../assets/images/s3.png"

const imageList = [img1, img2, img3]

class SelectMultipleScreens extends Component{
    constructor(props) {
        super(props)
        this.state = {
            image: '',
            images: [],
            max_images: [],
            max_message: ''
        }
        this.onPickMultipleImages = this.onPickMultipleImages.bind(this)
    }
    onPickMultipleImages(images) {
        this.setState({images})

    }

    // onPickImagesWithLimit(max_images) {
    //     this.setState({max_images})
    // }

    // onPickMaxImages(last_image) {
    //     let image = JSON.stringify(last_image)
    //     let max_message = `Max images reached. ${image}`
    //
    //     this.setState({max_message})
    // }

    render() {
        return (
            <div>
                <ImagePicker
                    images={imageList.map((image, i) => ({src: image, value: i}))}
                    onPick={this.onPickMultipleImages}
                    multiple
                />
                <button type="button" onClick={() => this.props.actionProvider.handleMultipleScreensOption()}>done</button>
                <button type="button" onClick={() => this.props.actionProvider.handleNoneOfAboveScreensOption()}>none of above</button>
                {/*<textarea rows="4" cols="100" value={this.state.images && JSON.stringify(this.state.images)} disabled/>*/}

            </div>
        )
    }
}
export default SelectMultipleScreens;