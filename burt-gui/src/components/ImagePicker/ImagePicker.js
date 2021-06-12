import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Map } from 'immutable'

import './index.scss'
import Image from './Image'

class ImagePicker extends Component {

    constructor(props) {
        super(props)
        this.state = {
            picked: Map()
        }
        this.handleImageClick = this.handleImageClick.bind(this)
        this.renderImage = this.renderImage.bind(this)
    }

    handleImageClick(image) {

        let {multiple, onPick, maxPicks, onMaxPicks} = this.props
        if (multiple) {
            let newerPickedImage;
            const pickedImage = this.state.picked;

            if (pickedImage.has(image.id)) {
                newerPickedImage = pickedImage.delete(image.id);

            } else {
                newerPickedImage = pickedImage.set(image.id, image.src);
            }
            if (newerPickedImage) {

                this.setState({picked: newerPickedImage}, function () {
                    const pickedImageToArray = []
                    this.state.picked.map((image, i) => pickedImageToArray.push({src: image.src, id: i, text:image.text}))
                    //console.log(pickedImageToArray)
                    onPick(pickedImageToArray)
                });
            }
        } else {
            let newerPickedImage;

            // pick single image
            let pickedImage = this.state.picked;
            if (pickedImage.has(image.id)) {
                pickedImage.delete(image.id);
                newerPickedImage = pickedImage;

            } else {
                pickedImage = Map();
                newerPickedImage = pickedImage.set(image.id, image.src);
            }
            this.setState({picked: newerPickedImage}, function () {
                const pickedImageToArray = []
                this.state.picked.map((image, i) => pickedImageToArray.push({src: image.src, id: i, text:image.text}))
                onPick(pickedImageToArray)
            });
        }

    }
    renderImage(image, i, style, selected, disabled) {

        return (
            <div key={image.id}>
                <Image
                    src={image.src}
                    isSelected={this.state.picked.has(image.id) || (selected !=null && selected.includes(image.id))}
                    onImageClick={() => this.handleImageClick(image)}
                    key={i}
                    style={style}
                    disabled = {disabled}
                />
                <p>{image.text}</p>
            </div>
        )
    }

    render() {
        const {images, style, selected, disabled} = this.props
        return (
            <div className="image_picker">
                { images.map( (image, i) => this.renderImage(image, i, style, selected, disabled)) }
                <div className="clear"/>
            </div>
        )
    }
}

ImagePicker.propTypes = {
    images: PropTypes.array,
    multiple: PropTypes.bool,
    onPick: PropTypes.func,
    maxPicks: PropTypes.number,
    onMaxPicks: PropTypes.func,
}

export default ImagePicker